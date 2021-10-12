package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class WhitespaceTokenParser implements TokenParser {
    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        for (boolean readNext = true; readNext; ) {
            readNext = Character.isWhitespace(characterBufferedReader.readNext()) && !characterBufferedReader.isDone();
        }

        return null;
    }
}
