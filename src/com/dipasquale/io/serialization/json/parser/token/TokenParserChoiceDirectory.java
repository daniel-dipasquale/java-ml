package com.dipasquale.io.serialization.json.parser.token;

import lombok.Getter;

import java.util.Set;

@Getter
public final class TokenParserChoiceDirectory {
    private static final TokenParserChoiceDirectory INSTANCE = new TokenParserChoiceDirectory();
    private final TokenParserChoice fileStart;
    private final TokenParserChoice whitespace;
    private final TokenParserChoice colon;
    private final TokenParserChoice comma;
    private final TokenParserChoice objectStart;
    private final TokenParserChoice objectEnd;
    private final TokenParserChoice arrayStart;
    private final TokenParserChoice arrayEnd;
    private final TokenParserChoice string;
    private final TokenParserChoice valueStart;

    private TokenParserChoiceDirectory() {
        TokenParser whitespaceParser = new WhitespaceTokenParser();
        TokenParser objectStartParser = new ObjectStartOrNextEntryTokenParser(this, true);
        TokenParser arrayStartParser = new ArrayStartOrNextElementTokenParser(this, true);
        TokenParser stringParser = new StringTokenParser();

        this.fileStart = createFileStartChoice(whitespaceParser, objectStartParser, arrayStartParser);
        this.whitespace = new OneTokenParserChoice(Character::isWhitespace, whitespaceParser, true);
        this.colon = new OneTokenParserChoice(Set.of(':')::contains, new OneCharacterTokenParser(), false);
        this.comma = new OneTokenParserChoice(Set.of(',')::contains, new OneCharacterTokenParser(), false);
        this.objectStart = new OneTokenParserChoice(Set.of('{')::contains, new OneCharacterTokenParser(), false);
        this.objectEnd = createObjectEndChoice(new ObjectStartOrNextEntryTokenParser(this, false), new FinalizeCurrentTokenParser());
        this.arrayStart = new OneTokenParserChoice(Set.of('[')::contains, new OneCharacterTokenParser(), false);
        this.arrayEnd = createArrayEndChoice(new ArrayStartOrNextElementTokenParser(this, false), new FinalizeCurrentTokenParser());
        this.string = new OneTokenParserChoice(Set.of('"')::contains, stringParser, false);
        this.valueStart = createValueStartChoice(objectStartParser, arrayStartParser, stringParser, new NumberTokenParser(), new BooleanTokenParser(), new NullTokenParser());
    }

    public static TokenParserChoiceDirectory getInstance() {
        return INSTANCE;
    }

    private static TokenParserChoice createObjectEndChoice(final TokenParser nextEntryTokenParser, final TokenParser objectEndParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(',', nextEntryTokenParser);
        tokenParserChoices.register('}', objectEndParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createArrayEndChoice(final TokenParser nextElementTokenParser, final TokenParser arrayEndParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(',', nextElementTokenParser);
        tokenParserChoices.register(']', arrayEndParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createFileStartChoice(final TokenParser whitespaceParser, final TokenParser objectStartParser, final TokenParser arrayStartParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(' ', whitespaceParser);
        tokenParserChoices.register('\t', whitespaceParser);
        tokenParserChoices.register('\r', whitespaceParser);
        tokenParserChoices.register('\n', whitespaceParser);
        tokenParserChoices.register('{', objectStartParser);
        tokenParserChoices.register('[', arrayStartParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createValueStartChoice(final TokenParser objectStartParser, final TokenParser arrayStartParser, final TokenParser stringParser, final TokenParser numberParser, final TokenParser booleanParser, final TokenParser nullParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register('{', objectStartParser);
        tokenParserChoices.register('[', arrayStartParser);
        tokenParserChoices.register('"', stringParser);
        tokenParserChoices.register('-', numberParser);
        tokenParserChoices.register('.', numberParser);

        for (int zero = '0', i = zero, c = zero + 10; i < c; i++) {
            tokenParserChoices.register((char) i, numberParser);
        }

        tokenParserChoices.register('f', booleanParser);
        tokenParserChoices.register('t', booleanParser);
        tokenParserChoices.register('n', nullParser);

        return tokenParserChoices;
    }
}
