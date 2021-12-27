package com.dipasquale.ai.rl.neat.core;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

public final class SupervisorTrainingPolicy implements NeatTrainingPolicy, Serializable {
    @Serial
    private static final long serialVersionUID = 1646221920253201871L;
    private final int maximumGeneration;
    private int iterationStoppedAt;
    private final int maximumRestartCount;

    @Builder
    public SupervisorTrainingPolicy(final int maximumGeneration, final int maximumRestartCount) {
        this.maximumGeneration = maximumGeneration;
        this.iterationStoppedAt = 0;
        this.maximumRestartCount = maximumRestartCount;
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (activator.getState().getIteration() - iterationStoppedAt - 1 > maximumRestartCount) {
            iterationStoppedAt = activator.getState().getIteration();

            return NeatTrainingResult.STOP_TRAINING;
        }

        if (activator.getState().getGeneration() > maximumGeneration) {
            return NeatTrainingResult.RESTART;
        }

        return NeatTrainingResult.CONTINUE_TRAINING;
    }

    @Override
    public void reset() {
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return new SupervisorTrainingPolicy(maximumGeneration, maximumRestartCount);
    }
}
