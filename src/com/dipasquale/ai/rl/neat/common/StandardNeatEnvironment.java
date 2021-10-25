package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class StandardNeatEnvironment implements FitnessFunction<GenomeActivator> {
    private final NeatEnvironment neatEnvironment;
    private final Map<String, FitnessBucket> fitnessBuckets;

    private static float normalize(final float fitness) {
        if (fitness == Float.NEGATIVE_INFINITY) {
            return 0f;
        }

        if (fitness == Float.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        return Math.max(fitness, 0f);
    }

    @Override
    public float test(final GenomeActivator genomeActivator) {
        float fitness = normalize(neatEnvironment.test(genomeActivator));
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());

        return fitnessBucket.addFitness(genomeActivator, fitness);
    }
}
