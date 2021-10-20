package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class StringTokenParser implements TokenParser {
    private static final Map<Character, Character> ESCAPED_CHARACTERS_ALLOWED = createEscapedCharactersAllowed();

    private static Map<Character, Character> createEscapedCharactersAllowed() {
        Map<Character, Character> escapedCharacters = new HashMap<>();

        escapedCharacters.put('b', '\b');
        escapedCharacters.put('f', '\f');
        escapedCharacters.put('n', '\n');
        escapedCharacters.put('r', '\r');
        escapedCharacters.put('t', '\t');
        escapedCharacters.put('"', '"');
        escapedCharacters.put('/', '/');
        escapedCharacters.put('\\', '\\');

        return escapedCharacters;
    }

    private static void addEscaped(final StringBuilder stringBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        char escapedCharacter = characterBufferedReader.readNext();

        switch (escapedCharacter) {
            case 'u' -> {
                StringBuilder hexBuilder = new StringBuilder();

                for (int i = 0, c = 4; i < c; i++) {
                    hexBuilder.append(characterBufferedReader.readNext());
                }

                String message = String.format("unable to parse the hex code (yet): '%s'", hexBuilder);

                throw new IOException(message);
            }

            default -> {
                Character character = ESCAPED_CHARACTERS_ALLOWED.get(escapedCharacter);

                if (character != null) {
                    stringBuilder.append(character);
                }

                String message = String.format("unable to parse the escaped character (yet): '%c'", escapedCharacter);

                throw new IOException(message);
            }
        }
    }

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        for (boolean readNext = true; readNext; ) {
            char character = characterBufferedReader.readNext();

            switch (character) {
                case '\\' -> addEscaped(stringBuilder, characterBufferedReader);

                case '"' -> readNext = false;

                default -> stringBuilder.append(character);
            }
        }

        jsonObjectBuilder.addString(stringBuilder.toString());

        if (!characterBufferedReader.isDone()) {
            characterBufferedReader.readNext();
        }

        return null;
    }
}
