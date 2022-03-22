package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ObjectAllEntriesTokenParser implements TokenParser {
    private final TokenParserChoiceDirectory tokenParserChoiceDirectory;

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader) {
        StackOnceTokenParserChoice tokenParserChoices = new StackOnceTokenParserChoice();

        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getObjectEndOrNextEntry());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getAnyValue());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getColon());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getString());

        return tokenParserChoices;
    }
}
