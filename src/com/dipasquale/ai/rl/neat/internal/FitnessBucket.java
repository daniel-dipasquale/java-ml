package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FitnessBucket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1790525419452423991L;
    private int iteration = 0;
    private int generation = 0;
    private final FitnessDeterminer fitnessDeterminer;

    private void ensureProperState(final GenomeActivator genomeActivator) {
        int currentIteration = genomeActivator.getIteration();
        int currentGeneration = genomeActivator.getGeneration();

        if (iteration != currentIteration || generation != currentGeneration) {
            iteration = currentIteration;
            generation = currentGeneration;
            fitnessDeterminer.clear();
        }
    }

    public float get(final GenomeActivator genomeActivator) {
        ensureProperState(genomeActivator);

        return fitnessDeterminer.get();
    }

    private static float normalize(final float fitness) {
        if (fitness == Float.NEGATIVE_INFINITY) {
            return 0f;
        }

        if (fitness == Float.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        return Math.max(fitness, 0f);
    }

    public float incorporate(final GenomeActivator genomeActivator, final float fitness) {
        ensureProperState(genomeActivator);
        fitnessDeterminer.add(normalize(fitness));

        return fitnessDeterminer.get();
    }
}
