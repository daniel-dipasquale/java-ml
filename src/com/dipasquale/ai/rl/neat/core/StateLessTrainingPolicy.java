package com.dipasquale.ai.rl.neat.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StateLessTrainingPolicy implements NeatTrainingPolicy {
    private final TrainingPolicyHandler handler;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        return handler.test(activator);
    }

    @Override
    public void reset() {
    }

    @FunctionalInterface
    public interface TrainingPolicyHandler {
        NeatTrainingResult test(NeatActivator activator);
    }
}
