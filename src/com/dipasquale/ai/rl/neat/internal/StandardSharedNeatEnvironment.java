package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.fitness.LastValueFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.core.SharedGenomeActivator;
import com.dipasquale.ai.rl.neat.core.SharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StandardSharedNeatEnvironment {
    private static final FitnessDeterminerFactory FITNESS_DETERMINER_FACTORY = new LastValueFitnessDeterminerFactory();
    private final SharedNeatEnvironment sharedNeatEnvironment;
    private final Map<String, FitnessBucket> fitnessBuckets;
    private final Map<String, FitnessBucket> transientFitnessBuckets;

    public StandardSharedNeatEnvironment(final SharedNeatEnvironment sharedNeatEnvironment, final Map<String, FitnessBucket> fitnessBuckets) {
        this.sharedNeatEnvironment = sharedNeatEnvironment;
        this.fitnessBuckets = fitnessBuckets;
        this.transientFitnessBuckets = createTransientFitnessBuckets(fitnessBuckets);
    }

    private static Map<String, FitnessBucket> createTransientFitnessBuckets(final Map<String, FitnessBucket> fitnessBuckets) {
        Map<String, FitnessBucket> transientFitnessBuckets = new HashMap<>();

        for (String genomeId : fitnessBuckets.keySet()) {
            FitnessBucket fitnessBucket = new FitnessBucket(FITNESS_DETERMINER_FACTORY.create());

            transientFitnessBuckets.put(genomeId, fitnessBucket);
        }

        return transientFitnessBuckets;
    }

    private float incorporateFitness(final GenomeActivator genomeActivator, final SharedGenomeActivator sharedGenomeActivator) {
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());
        float fitness = sharedGenomeActivator.getFitness(genomeActivator);

        return fitnessBucket.incorporate(genomeActivator, fitness);
    }

    public List<Float> test(final List<GenomeActivator> genomeActivators) {
        SharedGenomeActivator sharedGenomeActivator = new SharedGenomeActivator(List.copyOf(genomeActivators), transientFitnessBuckets);

        sharedNeatEnvironment.test(sharedGenomeActivator);

        return genomeActivators.stream()
                .map(ga -> incorporateFitness(ga, sharedGenomeActivator))
                .collect(Collectors.toList());
    }
}
