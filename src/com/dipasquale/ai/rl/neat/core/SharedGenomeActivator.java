package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.internal.FitnessBucket;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class SharedGenomeActivator {
    @Getter
    private final List<GenomeActivator> genomeActivators;
    private final Map<String, FitnessBucket> fitnessBuckets;

    private FitnessBucket getFitnessBucket(final GenomeActivator genomeActivator) {
        String genomeId = genomeActivator.getGenome().getId();

        return fitnessBuckets.get(genomeId);
    }

    public float getFitness(final GenomeActivator genomeActivator) {
        return getFitnessBucket(genomeActivator).get(genomeActivator);
    }

    public void putFitness(final GenomeActivator genomeActivator, final float fitness) {
        getFitnessBucket(genomeActivator).incorporate(genomeActivator, fitness);
    }
}
