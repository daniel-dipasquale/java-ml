package com.dipasquale.io;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardIOClient implements AutoCloseable {
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(?<name>[^:\\s]+)\\s*:\\s*(?<value>[^\\r\\n]*)$");
    private static final String END_MESSAGE_TOKEN = "END-MSG";
    private static final String TERMINATE_TOKEN = "TERMINATE";
    private static final String SERVER_ERROR_MESSAGE = "an error occurred in the remote server";
    private static final String UNSUPPORTED_STATUS_CODE_MESSAGE = "a status code not yet supported was found in the response";
    private final ProcessBuilder processBuilder;
    private Process process = null;
    private PrintWriter outputWriter = null;
    private Scanner inputReader = null;
    private boolean closed;

    @Builder
    private static StandardIOClient create(final String directoryName, final String processFileName, final List<String> arguments) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String[] commands = new String[arguments.size() + 1];

        commands[0] = processFileName;

        for (int i = 0, c = arguments.size(); i < c; i++) {
            commands[i + 1] = arguments.get(i);
        }

        processBuilder.directory(new File(directoryName));
        processBuilder.command(commands);

        return new StandardIOClient(processBuilder);
    }

    private void ensureInitialized()
            throws FailedToConnectException {
        if (process == null) {
            try {
                process = processBuilder.start();
                outputWriter = new PrintWriter(process.getOutputStream(), false);
                inputReader = new Scanner(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                throw new FailedToConnectException("unable to start the server process", e);
            }
        }
    }

    private void flushMapping(final StandardIORequest request) {
        String mapping = String.format("%s %s", request.getMethod().getValue(), request.getResource());

        outputWriter.print(mapping);
        outputWriter.print(System.lineSeparator());
        outputWriter.flush();
    }

    private void flushHeaders(final StandardIORequest request) {
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            String text = String.format("%s: %s", header.getKey(), header.getValue());

            outputWriter.print(text);
            outputWriter.print(System.lineSeparator());
            outputWriter.flush();
        }
    }

    private void flushBody(final StandardIORequest request) {
        if (!request.getBody().isEmpty()) {
            for (String text : request.getBody().split(System.lineSeparator())) {
                outputWriter.print(text);
                outputWriter.print(System.lineSeparator());
                outputWriter.flush();
            }
        }
    }

    private void sendRequest(final StandardIORequest request) {
        flushMapping(request);
        flushHeaders(request);

        if (request.getMethod() == RequestMethod.POST) {
            outputWriter.print("=");
            outputWriter.print(System.lineSeparator());
            outputWriter.flush();
            flushBody(request);
        }

        outputWriter.print(END_MESSAGE_TOKEN);
        outputWriter.print(System.lineSeparator());
        outputWriter.flush();
    }

    private StandardIOResponse parseResponse()
            throws ConnectionClosedException, InvalidResponseException, ServerErrorException, UnsupportedStatusCodeException {
        if (!inputReader.hasNextLine()) {
            throw new ConnectionClosedException("connection was closed");
        }

        String statusCodeText = inputReader.nextLine();
        int statusCode = Integer.parseInt(statusCodeText);
        Map<String, String> headers = new HashMap<>();
        boolean parsingHeaders = true;
        StringBuilder bodyBuilder = new StringBuilder();

        for (boolean reachedEndOfMessage = false; !reachedEndOfMessage && inputReader.hasNextLine(); ) {
            String text = inputReader.nextLine();
            reachedEndOfMessage = text.equals(END_MESSAGE_TOKEN);

            if (!reachedEndOfMessage) {
                if (parsingHeaders) {
                    if (!text.equals("=")) {
                        Matcher textMatcher = HEADER_PATTERN.matcher(text);

                        if (!textMatcher.find()) {
                            String message = String.format("invalid header: %s", text);

                            throw new InvalidResponseException(message);
                        }

                        headers.put(textMatcher.group("name"), textMatcher.group("value"));
                    } else {
                        parsingHeaders = false;
                    }
                } else {
                    if (bodyBuilder.length() > 0) {
                        bodyBuilder.append(System.lineSeparator());
                    }

                    bodyBuilder.append(text);
                }
            }
        }

        if (statusCode >= 200 && statusCode < 300) {
            return new StandardIOResponse(statusCode, headers, bodyBuilder.toString());
        }

        if (statusCode >= 500 && statusCode < 600) {
            throw new ServerErrorException(SERVER_ERROR_MESSAGE, statusCode, headers, bodyBuilder.toString());
        }

        throw new UnsupportedStatusCodeException(UNSUPPORTED_STATUS_CODE_MESSAGE, statusCode, headers, bodyBuilder.toString());
    }

    public StandardIOResponse invoke(final StandardIORequest request)
            throws FailedToConnectException, ConnectionClosedException, ServerErrorException, InvalidResponseException, UnsupportedStatusCodeException {
        ensureInitialized();
        sendRequest(request);

        return parseResponse();
    }

    @Override
    public void close()
            throws Exception {
        if (closed) {
            return;
        }

        closed = true;

        if (process != null) {
            inputReader.close();
            outputWriter.print(TERMINATE_TOKEN);
            outputWriter.print(System.lineSeparator());
            outputWriter.flush();
            outputWriter.close();

            if (!process.waitFor(100L, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
            }
        }
    }
}
