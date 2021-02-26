package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IdFactory;
import com.dipasquale.common.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GeneralSupportDefault<T extends Comparable<T>> implements Context.GeneralSupport<T> {
    private final int populationSize;
    private final int maximumGenerations;
    private final IdFactory<String> genomeIdFactory;
    private final ObjectFactory<GenomeDefault<T>> genesisGenomeFactory;
    private final FitnessCalculator fitnessCalculator;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public int maximumGenerations() {
        return maximumGenerations;
    }

    @Override
    public String createGenomeId() {
        return genomeIdFactory.createId();
    }

    @Override
    public GenomeDefault<T> createGenesisGenome() {
        return genesisGenomeFactory.create();
    }

    @Override
    public float calculateFitness(final GenomeDefault<T> genome) {
        return fitnessCalculator.calculate(genome);
    }
}
