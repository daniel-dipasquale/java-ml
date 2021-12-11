package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class SharedGenomeActivator {
    @Getter
    private final List<GenomeActivator> genomeActivators;
    private final Map<GenomeActivator, Float> fitnessByGenomeActivators = new IdentityHashMap<>();

    public float getFitness(final GenomeActivator genomeActivator) {
        return fitnessByGenomeActivators.getOrDefault(genomeActivator, 0f);
    }

    public void putFitness(final GenomeActivator genomeActivator, final float fitness) {
        fitnessByGenomeActivators.put(genomeActivator, fitness);
    }
}
