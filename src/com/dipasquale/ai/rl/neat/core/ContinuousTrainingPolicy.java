package com.dipasquale.ai.rl.neat.core;

import lombok.Builder;

public final class ContinuousTrainingPolicy implements NeatTrainingPolicy {
    private int cycle;
    private final int fitnessEvaluationCycle;

    @Builder
    public ContinuousTrainingPolicy(final int fitnessEvaluationCount) {
        this.cycle = 1;
        this.fitnessEvaluationCycle = fitnessEvaluationCount + 1;
    }

    public ContinuousTrainingPolicy() {
        this(1);
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        try {
            if (cycle == 0) {
                return NeatTrainingResult.EVOLVE;
            }

            return NeatTrainingResult.EVALUATE_FITNESS;
        } finally {
            cycle = (cycle + 1) % fitnessEvaluationCycle;
        }
    }

    @Override
    public void reset() {
        cycle = 1;
    }
}
