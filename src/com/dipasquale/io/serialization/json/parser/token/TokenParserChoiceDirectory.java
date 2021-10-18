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
    private final TokenParserChoice objectEndOrAllEntries;
    private final TokenParserChoice objectEndOrNextEntry;
    private final TokenParserChoice arrayStart;
    private final TokenParserChoice arrayEndOrAllElements;
    private final TokenParserChoice arrayEndOrNextElement;
    private final TokenParserChoice string;
    private final TokenParserChoice anyValue;

    private TokenParserChoiceDirectory() {
        TokenParser whitespaceParser = new WhitespaceTokenParser();
        TokenParser objectParser = new ObjectStartOrNextEntryTokenParser(this, true);
        TokenParser objectAllEntriesParser = new ObjectAllEntriesTokenParser(this);
        TokenParser arrayParser = new ArrayStartOrNextElementTokenParser(this, true);
        TokenParser arrayAllElementsParser = new ArrayAllElementsTokenParser(this);
        TokenParser stringParser = new StringTokenParser();
        TokenParser objectOrArrayEndParser = new FinalizeObjectOrArrayTokenParser();

        this.fileStart = createFileStart(whitespaceParser, objectParser, arrayParser);
        this.whitespace = new SingleTokenParserChoice(Character::isWhitespace, whitespaceParser, true);
        this.colon = new SingleTokenParserChoice(Set.of(':')::contains, new SingleCharacterTokenParser(), false);
        this.comma = new SingleTokenParserChoice(Set.of(',')::contains, new SingleCharacterTokenParser(), false);
        this.objectStart = new SingleTokenParserChoice(Set.of('{')::contains, new SingleCharacterTokenParser(), false);
        this.objectEndOrAllEntries = createObjectEndOrAllEntries(objectOrArrayEndParser, objectAllEntriesParser);
        this.objectEndOrNextEntry = createObjectEndOrNextEntry(objectOrArrayEndParser, new ObjectStartOrNextEntryTokenParser(this, false));
        this.arrayStart = new SingleTokenParserChoice(Set.of('[')::contains, new SingleCharacterTokenParser(), false);
        this.arrayEndOrAllElements = createArrayEndOrAllElements(objectOrArrayEndParser, arrayAllElementsParser);
        this.arrayEndOrNextElement = createArrayEndOrNextElement(objectOrArrayEndParser, new ArrayStartOrNextElementTokenParser(this, false));
        this.string = new SingleTokenParserChoice(Set.of('"')::contains, stringParser, false);
        this.anyValue = createAnyValue(objectParser, arrayParser, stringParser, new NumberTokenParser(), new BooleanTokenParser(), new NullTokenParser());
    }

    public static TokenParserChoiceDirectory getInstance() {
        return INSTANCE;
    }

    private static TokenParserChoice createObjectEndOrAllEntries(final TokenParser objectEndParser, final TokenParser objectAllEntriesParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register('}', objectEndParser);
        tokenParserChoices.register('"', objectAllEntriesParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createObjectEndOrNextEntry(final TokenParser objectEndParser, final TokenParser objectEntryParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register('}', objectEndParser);
        tokenParserChoices.register(',', objectEntryParser);

        return tokenParserChoices;
    }

    private static void registerObjectParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser objectParser) {
        tokenParserChoices.register('{', objectParser);
    }

    private static void registerArrayParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser arrayParser) {
        tokenParserChoices.register('[', arrayParser);
    }

    private static void registerStringParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser stringParser) {
        tokenParserChoices.register('"', stringParser);
    }

    private static void registerNumberParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser numberParser) {
        tokenParserChoices.register('-', numberParser);
        tokenParserChoices.register('.', numberParser);

        for (int zero = '0', i = zero, c = zero + 10; i < c; i++) {
            tokenParserChoices.register((char) i, numberParser);
        }
    }

    private static void registerBooleanParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser booleanParser) {
        tokenParserChoices.register('f', booleanParser);
        tokenParserChoices.register('t', booleanParser);
    }

    private static void registerNullParser(final ConfigurableTokenParserChoice tokenParserChoices, final TokenParser nullParser) {
        tokenParserChoices.register('n', nullParser);
    }

    private static TokenParserChoice createArrayEndOrAllElements(final TokenParser arrayEndParser, final TokenParser arrayAllElementsParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(']', arrayEndParser);
        registerObjectParser(tokenParserChoices, arrayAllElementsParser);
        registerArrayParser(tokenParserChoices, arrayAllElementsParser);
        registerStringParser(tokenParserChoices, arrayAllElementsParser);
        registerNumberParser(tokenParserChoices, arrayAllElementsParser);
        registerBooleanParser(tokenParserChoices, arrayAllElementsParser);
        registerNullParser(tokenParserChoices, arrayAllElementsParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createArrayEndOrNextElement(final TokenParser arrayEndParser, final TokenParser elementTokenParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(']', arrayEndParser);
        tokenParserChoices.register(',', elementTokenParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createFileStart(final TokenParser whitespaceParser, final TokenParser objectParser, final TokenParser arrayParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        tokenParserChoices.register(' ', whitespaceParser);
        tokenParserChoices.register('\t', whitespaceParser);
        tokenParserChoices.register('\r', whitespaceParser);
        tokenParserChoices.register('\n', whitespaceParser);
        registerObjectParser(tokenParserChoices, objectParser);
        registerArrayParser(tokenParserChoices, arrayParser);

        return tokenParserChoices;
    }

    private static TokenParserChoice createAnyValue(final TokenParser objectParser, final TokenParser arrayParser, final TokenParser stringParser, final TokenParser numberParser, final TokenParser booleanParser, final TokenParser nullParser) {
        ConfigurableTokenParserChoice tokenParserChoices = new ConfigurableTokenParserChoice();

        registerObjectParser(tokenParserChoices, objectParser);
        registerArrayParser(tokenParserChoices, arrayParser);
        registerStringParser(tokenParserChoices, stringParser);
        registerNumberParser(tokenParserChoices, numberParser);
        registerBooleanParser(tokenParserChoices, booleanParser);
        registerNullParser(tokenParserChoices, nullParser);

        return tokenParserChoices;
    }
}
