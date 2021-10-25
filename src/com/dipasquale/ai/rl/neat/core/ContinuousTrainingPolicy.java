package com.dipasquale.ai.rl.neat.core;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

public final class ContinuousTrainingPolicy implements NeatTrainingPolicy, Serializable {
    @Serial
    private static final long serialVersionUID = -5091173028334364296L;
    private int cycle;
    private final int fitnessEvaluationCycle;

    @Builder
    public ContinuousTrainingPolicy(final int fitnessTestCount) {
        this.cycle = 1;
        this.fitnessEvaluationCycle = fitnessTestCount + 1;
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

    @Override
    public NeatTrainingPolicy createClone() {
        return new ContinuousTrainingPolicy(fitnessEvaluationCycle - 1);
    }
}
