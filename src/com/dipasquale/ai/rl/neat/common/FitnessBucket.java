package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
public final class FitnessBucket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1790525419452423991L;
    private int generation;
    private final FitnessDeterminer fitnessDeterminer;

    public void updateGeneration(final int value) {
        if (generation != value) {
            generation = value;
            fitnessDeterminer.clear();
        }
    }

    public float addFitness(final float fitness) {
        fitnessDeterminer.add(fitness);

        return fitnessDeterminer.get();
    }
}
