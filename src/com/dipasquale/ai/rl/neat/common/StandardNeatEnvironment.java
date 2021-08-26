package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class StandardNeatEnvironment implements FitnessFunction<Genome> {
    private final NeatEnvironment neatEnvironment;
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final Map<String, FitnessBucket> fitnessBuckets;

    private static float normalize(final float fitness) {
        if (fitness == Float.NEGATIVE_INFINITY) {
            return 0f;
        }

        if (fitness == Float.POSITIVE_INFINITY) {
            return Float.MAX_VALUE;
        }

        return Math.max(fitness, 0f); // TODO: review this in the context of shared-fitness and adjusted-fitness
    }

    private FitnessBucket createFitnessBucket(final Genome genome) {
        return new FitnessBucket(genome.getGeneration(), fitnessDeterminerFactory.create());
    }

    @Override
    public float test(final Genome genome) {
        float fitness = normalize(neatEnvironment.test(genome));
        FitnessBucket fitnessBucket = fitnessBuckets.computeIfAbsent(genome.getId(), gid -> createFitnessBucket(genome));

        fitnessBucket.updateGeneration(genome.getGeneration());

        return fitnessBucket.addFitness(fitness);
    }
}
