package com.dipasquale.ai.common.fitness;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.common.LimitSupport;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FitnessBucket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1790525419452423991L;
    private int iteration = 0;
    private int generation = 0;
    private final FitnessController fitnessController;

    private void ensureProperState(final GenomeActivator genomeActivator) {
        int currentIteration = genomeActivator.getIteration();
        int currentGeneration = genomeActivator.getGeneration();

        if (iteration != currentIteration || generation != currentGeneration) {
            iteration = currentIteration;
            generation = currentGeneration;
            fitnessController.clear();
        }
    }

    public float get(final GenomeActivator genomeActivator) {
        ensureProperState(genomeActivator);

        return fitnessController.get();
    }

    public float incorporate(final GenomeActivator genomeActivator, final float fitness) {
        ensureProperState(genomeActivator);
        fitnessController.add(LimitSupport.getPositiveFiniteValue(fitness));

        return fitnessController.get();
    }
}
