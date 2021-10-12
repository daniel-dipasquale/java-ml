package com.dipasquale.io.serialization.json.parser;

import com.dipasquale.io.CharacterBufferedReader;
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

    private static UnableToParseJsonException createUnableToParseException(final CharacterBufferedReader characterBufferedReader) {
        String message = String.format("unable to parse character '%c' at location: %d", characterBufferedReader.getCurrent(), characterBufferedReader.getIndex());

        return new UnableToParseJsonException(message);
    }

    private JsonObject parse(final JsonObjectBuilder jsonObjectBuilder, CharacterBufferedReader characterBufferedReader)
            throws IOException {
        TokenParser tokenParser = TOKEN_PARSER_CHOICE_DIRECTORY.getFileStart().get(characterBufferedReader.readNext());
        TokenParserChoice tokenParserChoice = tokenParser.parse(jsonObjectBuilder, characterBufferedReader);

        while (!characterBufferedReader.isDone() && tokenParserChoice != null && tokenParserChoice.repeatable() > 0) {
            tokenParser = tokenParserChoice.get(characterBufferedReader.getCurrent());

            if (tokenParser != null) {
                tokenParserChoice = merge(tokenParserChoice, tokenParser.parse(jsonObjectBuilder, characterBufferedReader));
            }
        }

        if (characterBufferedReader.isDone()) {
            return jsonObjectBuilder.build();
        }

        throw createUnableToParseException(characterBufferedReader);
    }

    public JsonObject parse(final Reader reader)
            throws IOException {
        JsonObjectBuilder jsonObjectBuilder = new JsonObjectBuilder();
        CharacterBufferedReader characterBufferedReader = new CharacterBufferedReader(reader, bufferSize);

        return parse(jsonObjectBuilder, characterBufferedReader);
    }

    public JsonObject parse(final String json)
            throws IOException {
        try (StringReader stringReader = new StringReader(json)) {
            return parse(stringReader);
        }
    }
}
