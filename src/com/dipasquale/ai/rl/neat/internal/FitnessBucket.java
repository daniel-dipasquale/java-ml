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
        if (iteration == genomeActivator.getIteration() && generation == genomeActivator.getGeneration()) {
            return;
        }

        iteration = genomeActivator.getIteration();
        generation = genomeActivator.getGeneration();
        fitnessDeterminer.clear();
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

    public float addFitness(final GenomeActivator genomeActivator, final float fitness) {
        ensureProperState(genomeActivator);
        fitnessDeterminer.add(normalize(fitness));

        return fitnessDeterminer.get();
    }
}
