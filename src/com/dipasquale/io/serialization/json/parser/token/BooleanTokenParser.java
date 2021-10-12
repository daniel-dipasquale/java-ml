package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class BooleanTokenParser implements TokenParser {
    private static void parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader, final String expected)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(characterBufferedReader.getCurrent());

        for (int i = 0, c = expected.length() - 1; i < c; i++) {
            stringBuilder.append(characterBufferedReader.readNext());
        }

        String value = stringBuilder.toString();

        if (!expected.equals(value)) {
            String message = String.format("illegal boolean: %s", value);

            throw new IllegalStateException(message);
        }

        jsonObjectBuilder.addBoolean(Boolean.parseBoolean(value));
    }

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        switch (characterBufferedReader.getCurrent()) {
            case 'f' -> parse(jsonObjectBuilder, characterBufferedReader, "false");

            default -> parse(jsonObjectBuilder, characterBufferedReader, "true");
        }

        if (!characterBufferedReader.isDone()) {
            characterBufferedReader.readNext();
        }

        return null;
    }
}
