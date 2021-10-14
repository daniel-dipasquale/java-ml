package com.dipasquale.io.serialization.json.parser;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObject;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import com.dipasquale.io.serialization.json.parser.token.StackOnceTokenParserChoice;
import com.dipasquale.io.serialization.json.parser.token.TokenParser;
import com.dipasquale.io.serialization.json.parser.token.TokenParserChoice;
import com.dipasquale.io.serialization.json.parser.token.TokenParserChoiceDirectory;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@RequiredArgsConstructor
public final class JsonParser {
    private static final int BUFFER_SIZE = 1_024;
    private static final TokenParserChoiceDirectory TOKEN_PARSER_CHOICE_DIRECTORY = TokenParserChoiceDirectory.getInstance();
    private final int bufferSize;

    public JsonParser() {
        this(BUFFER_SIZE);
    }

    private static TokenParserChoice merge(final TokenParserChoice tokenParserChoice1, final TokenParserChoice tokenParserChoice2) {
        if (tokenParserChoice2 == null) {
            return tokenParserChoice1;
        }

        StackOnceTokenParserChoice tokenParserChoices = new StackOnceTokenParserChoice();

        tokenParserChoices.push(tokenParserChoice1);
        tokenParserChoices.push(tokenParserChoice2);

        return tokenParserChoices;
    }

    private JsonObject parse(final JsonObjectBuilder jsonObjectBuilder, CharacterBuffer characterBuffer)
            throws IOException {
        TokenParser tokenParser = TOKEN_PARSER_CHOICE_DIRECTORY.getFileStart().get(characterBuffer.readNext());
        TokenParserChoice tokenParserChoice = tokenParser.parse(jsonObjectBuilder, characterBuffer);

        while (!characterBuffer.isDone() && tokenParserChoice != null && tokenParserChoice.repeatable() > 0) {
            tokenParser = tokenParserChoice.get(characterBuffer.getCurrent());

            if (tokenParser != null) {
                tokenParserChoice = merge(tokenParserChoice, tokenParser.parse(jsonObjectBuilder, characterBuffer));
            }
        }

        if (characterBuffer.isDone()) {
            return jsonObjectBuilder.build();
        }

        throw new IllegalStateException("unable to parse the entire json");
    }

    private static IOException createUnableToParseException(final CharacterBuffer characterBuffer, final Throwable exception) {
        String message = String.format("unable to parse character '%c' at location: %d", characterBuffer.getCurrent(), characterBuffer.getIndex());

        return new IOException(message, exception);
    }

    public JsonObject parse(final Reader reader)
            throws IOException {
        JsonObjectBuilder jsonObjectBuilder = new JsonObjectBuilder();
        CharacterBuffer characterBuffer = new CharacterBuffer(reader, bufferSize);

        try {
            return parse(jsonObjectBuilder, characterBuffer);
        } catch (Exception e) {
            throw createUnableToParseException(characterBuffer, e);
        }
    }

    public JsonObject parse(final String json)
            throws IOException {
        try (StringReader stringReader = new StringReader(json)) {
            return parse(stringReader);
        }
    }
}
