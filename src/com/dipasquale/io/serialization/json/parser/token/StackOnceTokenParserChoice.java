package com.dipasquale.io.serialization.json.parser.token;

import lombok.RequiredArgsConstructor;

import java.util.Stack;

@RequiredArgsConstructor
public final class StackOnceTokenParserChoice implements TokenParserChoice {
    private final Stack<TokenParserChoiceTracker> trackers = new Stack<>();
    private int repeatable = 0;

    public void push(final TokenParserChoice tokenParserChoice) {
        if (tokenParserChoice == null || tokenParserChoice.repeatable() == 0) {
            return;
        }

        trackers.push(new TokenParserChoiceTracker(tokenParserChoice));
        repeatable += tokenParserChoice.repeatable();
    }

    @Override
    public int repeatable() {
        return repeatable;
    }

    @Override
    public TokenParser get(final char character) {
        if (trackers.isEmpty()) {
            return null;
        }

        TokenParserChoiceTracker tracker = trackers.peek();

        if (--tracker.repeatable == 0) {
            trackers.pop();
        }

        repeatable--;

        return tracker.tokenParserChoice.get(character);
    }

    private static final class TokenParserChoiceTracker {
        private final TokenParserChoice tokenParserChoice;
        private int repeatable;

        private TokenParserChoiceTracker(final TokenParserChoice tokenParserChoice) {
            this.tokenParserChoice = tokenParserChoice;
            this.repeatable = tokenParserChoice.repeatable();
        }
    }
}
