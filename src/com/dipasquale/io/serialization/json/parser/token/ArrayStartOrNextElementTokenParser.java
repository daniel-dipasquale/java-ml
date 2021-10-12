package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBufferedReader;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import com.dipasquale.io.serialization.json.JsonObjectType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ArrayStartOrNextElementTokenParser implements TokenParser {
    private final TokenParserChoiceDirectory tokenParserChoiceDirectory;
    private final boolean createNew;

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBufferedReader characterBufferedReader)
            throws IOException {
        StackOnceTokenParserChoice tokenParserChoices = new StackOnceTokenParserChoice();

        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getArrayEndOrNextEntry());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getValueStart());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());

        if (createNew) {
            tokenParserChoices.push(tokenParserChoiceDirectory.getArrayStart());
            jsonObjectBuilder.addObject(JsonObjectType.ARRAY);
        } else {
            tokenParserChoices.push(tokenParserChoiceDirectory.getComma());
        }

        return tokenParserChoices;
    }
}
