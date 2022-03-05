package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class InvalidTrainingPolicy implements NeatTrainingPolicy {
    private static final InvalidTrainingPolicy INSTANCE = new InvalidTrainingPolicy();

    public static InvalidTrainingPolicy getInstance() {
        return INSTANCE;
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return INSTANCE;
    }
}
