package com.dipasquale.simulation.openai.gym.client;

import com.dipasquale.io.IORuntimeException;
import com.dipasquale.io.serialization.json.JsonObject;
import com.dipasquale.io.serialization.json.JsonObjectType;
import com.dipasquale.io.serialization.json.parser.JsonParser;
import com.dipasquale.io.serialization.json.writer.JsonWriter;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InternalGymClient implements Closeable {
    private static final String ENVIRONMENTS_API = "/v1/envs/";
    private static final String ENVIRONMENTS_RESET_API = "/v1/envs/%s/reset/";
    private static final String ENVIRONMENTS_ACTION_SPACE_API = "/v1/envs/%s/action_space/";
    private static final String ENVIRONMENTS_STEP_API = "/v1/envs/%s/step/";
    private static final String ENVIRONMENTS_MONITOR_START_API = "/v1/envs/%s/monitor/start/";
    private static final String ENVIRONMENTS_MONITOR_CLOSE_API = "/v1/envs/%s/monitor/close/";
    private static final String UPLOAD_API = "/v1/upload/";
    private static final String SHUTDOWN_API = "/v1/shutdown/";
    private static final ResponseHandler<Void> VOID_RESPONSE_HANDLER = new ResponseHandler<>(null, InternalGymClient::drainBody);
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final JsonWriter JSON_WRITER = JsonWriter.getInstance();
    private static final JsonObject EMPTY_BODY = new JsonObject(JsonObjectType.OBJECT);
    private static final int BUFFER_SIZE = 1_024;
    private final HttpClient httpClient = createHttpClient();
    private final String baseUrl;
    private final JsonParser jsonParser = new JsonParser(BUFFER_SIZE);
    private final ResponseHandler<JsonObject> jsonResponseHandler = new ResponseHandler<>(JSON_CONTENT_TYPE, this::parseBody);

    private static String extractHeader(final Map<String, List<String>> headers, final String name) {
        List<String> values = headers.get(name);

        if (values == null) {
            return null;
        }

        StringJoiner stringJoiner = new StringJoiner(",");

        values.forEach(stringJoiner::add);

        return stringJoiner.toString();
    }

    private static Map<String, String> flattenHeaders(final Map<String, List<String>> headers) {
        Map<String, String> headersFixed = new HashMap<>();

        for (String key : headers.keySet()) {
            String value = extractHeader(headers, key);

            headersFixed.put(key, value);
        }

        return headersFixed;
    }

    private JsonObject parseBody(final HttpResponse<InputStream> response)
            throws IOException {
        try (InputStream inputStream = response.body();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return jsonParser.parse(inputStreamReader);
        }
    }

    private static String readBody(final HttpResponse<InputStream> response)
            throws IOException {
        try (InputStream inputStream = response.body();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];

            for (int length = inputStream.read(buffer); length != -1; length = inputStream.read(buffer)) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toString(StandardCharsets.UTF_8);
        }
    }

    private static Void drainBody(final HttpResponse<InputStream> response)
            throws IOException {
        readBody(response);

        return null;
    }

    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private <T> T invoke(final HttpRequest request, final ResponseHandler<T> responseHandler) {
        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();
            Map<String, List<String>> headers = response.headers().map();
            String contentType = extractHeader(headers, "content-type");
            boolean success = statusCode >= 200 && statusCode < 300;
            boolean compliant = responseHandler.ableToHandle(contentType);

            if (success && compliant) {
                return responseHandler.handle(response);
            }

            Map<String, String> headersFixed = Collections.unmodifiableMap(flattenHeaders(headers));

            if (success) {
                throw new GymRequestException("the request succeeded but the body for the response was not in JSON format", statusCode, headersFixed, readBody(response));
            }

            if (compliant) {
                throw new GymRequestException("the request failed", statusCode, headersFixed, readBody(response));
            }

            throw new GymRequestException("the request failed and the body for the response was not in JSON format", statusCode, headersFixed, readBody(response));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("the request was interrupted", e);
        } catch (IOException e) {
            throw new IORuntimeException("unknown exception occurred while making the request", e);
        }
    }

    private HttpRequest createGetRequest(final String url) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("content-type", JSON_CONTENT_TYPE)
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException(url, e);
        }
    }

    private static <T> Map<String, T> translateToMap(final JsonObject jsonObject) {
        return StreamSupport.stream(jsonObject.spliterator(), false)
                .map(Object::toString)
                .collect(Collectors.toMap(key -> key, key -> (T) jsonObject.get(key)));
    }

    public Map<String, String> getEnvironments() {
        String url = String.format("%s%s", baseUrl, ENVIRONMENTS_API);
        HttpRequest request = createGetRequest(url);
        JsonObject response = invoke(request, jsonResponseHandler);

        return translateToMap((JsonObject) response.get("all_envs"));
    }

    private static HttpRequest.BodyPublisher createBodyPublisher(final JsonObject body) {
        try {
            String json = JSON_WRITER.toString(body);

            return HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UnableToCreatePostBodyException(body, e);
        }
    }

    private static HttpRequest createPostRequest(final String url, final JsonObject body) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("content-type", JSON_CONTENT_TYPE)
                    .POST(createBodyPublisher(body))
                    .build();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException(url, e);
        }
    }

    public String createEnvironment(final String environmentId) {
        String url = String.format("%s%s", baseUrl, ENVIRONMENTS_API);
        JsonObject body = new JsonObject(JsonObjectType.OBJECT);

        body.put("env_id", environmentId);

        HttpRequest request = createPostRequest(url, body);
        JsonObject response = invoke(request, jsonResponseHandler);

        return (String) response.get("instance_id");
    }

    private static double[] translateToDoubleArray(final JsonObject jsonObject) {
        return StreamSupport.stream(jsonObject.spliterator(), false)
                .map(key -> (Integer) key)
                .mapToDouble(index -> (double) jsonObject.get(index))
                .toArray();
    }

    public double[] reset(final String instanceId) {
        String url = String.format("%s%s", baseUrl, String.format(ENVIRONMENTS_RESET_API, instanceId));
        HttpRequest request = createPostRequest(url, EMPTY_BODY);
        JsonObject response = invoke(request, jsonResponseHandler);

        return translateToDoubleArray((JsonObject) response.get("observation"));
    }

    public Map<String, Object> getActionSpace(final String instanceId) {
        String url = String.format("%s%s", baseUrl, String.format(ENVIRONMENTS_ACTION_SPACE_API, instanceId));
        HttpRequest request = createGetRequest(url);
        JsonObject response = invoke(request, jsonResponseHandler);

        return translateToMap((JsonObject) response.get("info"));
    }

    public StepResult step(final String instanceId, final double action, final boolean render) {
        String url = String.format("%s%s", baseUrl, String.format(ENVIRONMENTS_STEP_API, instanceId));
        JsonObject body = new JsonObject(JsonObjectType.OBJECT);

        body.put("action", action);
        body.put("render", render);

        HttpRequest request = createPostRequest(url, body);
        JsonObject response = invoke(request, jsonResponseHandler);

        return StepResult.builder()
                .done((boolean) response.get("done"))
                .observation(translateToDoubleArray((JsonObject) response.get("observation")))
                .reward(Double.parseDouble(response.get("reward").toString()))
                .build();
    }

    public void startMonitor(final String instanceId, final boolean force, final boolean resume) {
        String url = String.format("%s%s", baseUrl, String.format(ENVIRONMENTS_MONITOR_START_API, instanceId));
        JsonObject body = new JsonObject(JsonObjectType.OBJECT);

        body.put("force", force);
        body.put("resume", resume);

        HttpRequest request = createPostRequest(url, body);

        invoke(request, VOID_RESPONSE_HANDLER);
    }

    public void closeMonitor(final String instanceId) {
        String url = String.format("%s%s", baseUrl, String.format(ENVIRONMENTS_MONITOR_CLOSE_API, instanceId));
        HttpRequest request = createPostRequest(url, EMPTY_BODY);

        invoke(request, VOID_RESPONSE_HANDLER);
    }

    public void upload(final String trainingDirectory, final String apiKey, final String algorithmId) {
        String url = String.format("%s%s", baseUrl, UPLOAD_API);
        JsonObject body = new JsonObject(JsonObjectType.OBJECT);

        body.put("training_dir", trainingDirectory);
        body.put("api_key", apiKey);

        if (algorithmId != null && !algorithmId.isEmpty()) {
            body.put("algorithm_id", algorithmId);
        } else {
            body.put("algorithm_id", "None");
        }

        HttpRequest request = createPostRequest(url, body);

        invoke(request, VOID_RESPONSE_HANDLER);
    }

    public void shutdown() {
        String url = String.format("%s%s", baseUrl, SHUTDOWN_API);
        HttpRequest request = createPostRequest(url, EMPTY_BODY);

        invoke(request, VOID_RESPONSE_HANDLER);
    }

    @Override
    public void close() {
    }
}
