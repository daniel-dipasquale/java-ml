package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NullTokenParser implements TokenParser {
    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBuffer characterBuffer)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(characterBuffer.getCurrent());

        for (int i = 0, c = 3; i < c; i++) {
            stringBuilder.append(characterBuffer.readNext());
        }

        String value = stringBuilder.toString();

        if (!"null".equals(value)) {
            String message = String.format("illegal null pointer: %s", value);

            throw new IllegalStateException(message);
        }

        jsonObjectBuilder.addNull();

        if (!characterBuffer.isDone()) {
            characterBuffer.readNext();
        }

        return null;
    }
}
