package com.dipasquale.io.serialization.json.parser.token;

public interface TokenParserChoice {
    int repeatable();

    TokenParser get(char character);
}
