package com.dipasquale.io.serialization.json.parser.token;

import com.dipasquale.io.CharacterBuffer;
import com.dipasquale.io.serialization.json.JsonObjectBuilder;
import com.dipasquale.io.serialization.json.JsonObjectType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ObjectStartOrNextEntryTokenParser implements TokenParser {
    private final TokenParserChoiceDirectory tokenParserChoiceDirectory;
    private final boolean createNew;

    @Override
    public TokenParserChoice parse(final JsonObjectBuilder jsonObjectBuilder, final CharacterBuffer characterBuffer)
            throws IOException {
        StackOnceTokenParserChoice tokenParserChoices = new StackOnceTokenParserChoice();

        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getObjectEnd());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getValueStart());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getColon());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());
        tokenParserChoices.push(tokenParserChoiceDirectory.getString());
        tokenParserChoices.push(tokenParserChoiceDirectory.getWhitespace());

        if (createNew) {
            tokenParserChoices.push(tokenParserChoiceDirectory.getObjectStart());
            jsonObjectBuilder.addObject(JsonObjectType.OBJECT);
        } else {
            tokenParserChoices.push(tokenParserChoiceDirectory.getComma());
        }

        return tokenParserChoices;
    }
}
