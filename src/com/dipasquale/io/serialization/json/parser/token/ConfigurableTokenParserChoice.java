package com.dipasquale.io.serialization.json.parser.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ConfigurableTokenParserChoice implements TokenParserChoice {
    private final Map<Character, TokenParser> tokenParsers = new HashMap<>();

    public void register(final char character, final TokenParser tokenParser) {
        tokenParsers.put(character, tokenParser);
    }

    @Override
    public int repeatable() {
        return 1;
    }

    @Override
    public TokenParser get(final char character) {
        TokenParser tokenParser = tokenParsers.get(character);

        if (tokenParser != null) {
            return tokenParser;
        }

        String message = String.format("token for character '%c' is not optional", character);

        throw new IllegalStateException(message);
    }
}
