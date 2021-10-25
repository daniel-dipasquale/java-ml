package com.dipasquale.ai.rl.neat.common;

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

    private void ensurePrepared(final GenomeActivator genomeActivator) {
        if (iteration == genomeActivator.getIteration() && generation == genomeActivator.getGeneration()) {
            return;
        }

        iteration = genomeActivator.getIteration();
        generation = genomeActivator.getGeneration();
        fitnessDeterminer.clear();
    }

    public float addFitness(final GenomeActivator genomeActivator, final float fitness) {
        ensurePrepared(genomeActivator);
        fitnessDeterminer.add(fitness);

        return fitnessDeterminer.get();
    }
}
