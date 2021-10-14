package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class WhitespaceTokenParser implements TokenParser {
    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBuffer characterBuffer)
            throws IOException {
        for (boolean readNext = true; readNext; ) {
            readNext = Character.isWhitespace(characterBuffer.readNext()) && !characterBuffer.isDone();
        }

        return null;
    }
}
