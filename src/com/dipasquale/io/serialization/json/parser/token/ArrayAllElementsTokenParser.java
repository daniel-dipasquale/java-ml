package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ArrayAllElementsTokenParser implements TokenParser {
    private final TokenParserChoiceDirectory tokenParserChoiceDirectory;

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader) {
        StackOnceTokenParserChoice tokenParserChoices = new StackOnceTokenParserChoice();

        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getArrayEndOrNextElement());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getAnyValue());

        return tokenParserChoices;
    }
}
