package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NumberTokenParser implements TokenParser {
    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int dataTypeStage = 0;
        char previousCharacter = characterBufferedReader.getCurrent();

        stringBuilder.append(previousCharacter);

        for (boolean readNext = true; readNext; ) {
            char character = characterBufferedReader.readNext();

            switch (character) {
                case '.', 'e', 'E', '+', '-' -> {
                    if (character == '.' && dataTypeStage > 0 || (character == '+' || character == '-') && (dataTypeStage < 2 || previousCharacter != 'e' && previousCharacter != 'E')) {
                        String message = String.format("invalid number '%s%c'", stringBuilder, character);

                        throw new NumberFormatException(message);
                    }

                    stringBuilder.append(character);

                    if (character == '.') {
                        dataTypeStage = 1;
                    } else {
                        dataTypeStage = 2;
                    }
                }

                default -> {
                    readNext = Character.isDigit(character);

                    if (readNext) {
                        stringBuilder.append(character);
                    }
                }
            }

            previousCharacter = character;
            readNext &= !characterBufferedReader.isDone();
        }

        String value = stringBuilder.toString();

        if (dataTypeStage > 0) {
            jsonObjectBuilder.addNumber(Double.parseDouble(value));
        } else {
            jsonObjectBuilder.addNumber(Long.parseLong(value));
        }

        return null;
    }
}
