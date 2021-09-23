package com.dipasquale.ai.rl.neat.core;

import lombok.Builder;

public final class SupervisorTrainingPolicy implements NeatTrainingPolicy {
    private final int maximumGeneration;
    private int restartCount;
    private final int maximumRestartCount;

    @Builder
    public SupervisorTrainingPolicy(final int maximumGeneration, final int maximumRestartCount) {
        this.maximumGeneration = maximumGeneration;
        this.restartCount = 0;
        this.maximumRestartCount = maximumRestartCount;
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (activator.getGeneration() < maximumGeneration) {
            return NeatTrainingResult.CONTINUE_TRAINING;
        }

        if (restartCount < maximumRestartCount) {
            restartCount++;

            return NeatTrainingResult.RESTART;
        }

        return NeatTrainingResult.STOP_TRAINING;
    }

    @Override
    public void reset() {
        restartCount = 0;
    }
}
