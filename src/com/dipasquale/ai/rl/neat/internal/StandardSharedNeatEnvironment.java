package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.rl.neat.core.SharedGenomeActivator;
import com.dipasquale.ai.rl.neat.core.SharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class StandardSharedNeatEnvironment {
    private final SharedNeatEnvironment sharedNeatEnvironment;
    private final Map<String, FitnessBucket> fitnessBuckets;

    private float addFitness(final GenomeActivator genomeActivator, final SharedGenomeActivator sharedGenomeActivator) {
        FitnessBucket fitnessBucket = fitnessBuckets.get(genomeActivator.getGenome().getId());
        float fitness = sharedGenomeActivator.getFitness(genomeActivator);

        return fitnessBucket.addFitness(genomeActivator, fitness);
    }

    public List<Float> test(final List<GenomeActivator> genomeActivators) {
        SharedGenomeActivator sharedGenomeActivator = new SharedGenomeActivator(List.copyOf(genomeActivators));

        sharedNeatEnvironment.test(sharedGenomeActivator);

        return genomeActivators.stream()
                .map(ga -> addFitness(ga, sharedGenomeActivator))
                .collect(Collectors.toList());
    }
}
