package com.dipasquale.ai.rl.neat.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MaximumGenerationsTrainingPolicy implements NeatTrainingPolicy {
    private final int maximumGeneration;
    private final NeatTrainingResult defaultResult;
    private int restartCount = 0;
    private final int maximumRestartCount;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (activator.getGeneration() < maximumGeneration) {
            return defaultResult;
        }

        if (restartCount < maximumRestartCount) {
            restartCount++;

            return NeatTrainingResult.RESTART;
        }

        return NeatTrainingResult.SOLUTION_NOT_FOUND;
    }

    @Override
    public void complete() {
        restartCount = 0;
    }
}
