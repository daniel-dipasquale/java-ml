package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NumberTokenParser implements TokenParser {
    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBuffer characterBuffer)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isDouble = false;

        stringBuilder.append(characterBuffer.getCurrent());

        for (boolean readNext = true; readNext; ) {
            char character = characterBuffer.readNext();

            switch (character) {
                case '.' -> {
                    if (isDouble) {
                        String message = String.format("invalid number '%s.'", stringBuilder);

                        throw new NumberFormatException(message);
                    }

                    stringBuilder.append('.');
                    isDouble = true;
                }

                default -> {
                    readNext = Character.isDigit(character);

                    if (readNext) {
                        stringBuilder.append(character);
                    }
                }
            }

            readNext &= !characterBuffer.isDone();
        }

        String value = stringBuilder.toString();

        if (isDouble) {
            jsonObjectBuilder.addNumber(Double.parseDouble(value));
        } else {
            jsonObjectBuilder.addNumber(Long.parseLong(value));
        }

        return null;
    }
}
