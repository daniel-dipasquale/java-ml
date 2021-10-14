package com.dipasquale.io.serialization.json.parser.token;

@FunctionalInterface
public interface TokenParserClassifier {
    boolean isValid(char character);
}
