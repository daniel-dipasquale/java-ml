package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.core.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class StandardNeatEnvironment implements FitnessFunction<GenomeActivator> {
    private final IsolatedNeatEnvironment isolatedNeatEnvironment;
    private final Map<String, FitnessBucket> fitnessBuckets;

    @Override
    public float test(final GenomeActivator genomeActivator) {
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());
        float fitness = isolatedNeatEnvironment.test(genomeActivator);

        return fitnessBucket.addFitness(genomeActivator, fitness);
    }
}
