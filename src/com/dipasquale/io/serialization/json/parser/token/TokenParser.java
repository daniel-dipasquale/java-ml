package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;

import java.io.IOException;

@FunctionalInterface
public interface TokenParser {
    TokenParserChoice parse(JsonObjectBuilder jsonObjectBuilder, CharacterBuffer characterBuffer) throws IOException;
}
