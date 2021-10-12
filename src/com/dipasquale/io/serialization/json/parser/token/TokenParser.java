package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;

import java.io.IOException;

@FunctionalInterface
public interface TokenParser {
    TokenParserChoice parse(JsonObjectBuilder jsonObjectBuilder, CharacterBufferedReader characterBufferedReader) throws IOException;
}
