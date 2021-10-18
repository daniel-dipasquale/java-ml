package com.dipasquale.io.serialization.json.parser.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class SingleTokenParserChoice implements TokenParserChoice {
    private final TokenParserClassifier tokenParserClassifier;
    private final TokenParser tokenParser;
    private final boolean optional;

    @Override
    public int repeatable() {
        return 1;
    }

    @Override
    public TokenParser get(final char character) {
        if (!tokenParserClassifier.isValid(character)) {
            if (optional) {
                return null;
            }

            String message = String.format("token for character '%c' is not optional", character);

            throw new IllegalStateException(message);
        }

        return tokenParser;
    }
}
