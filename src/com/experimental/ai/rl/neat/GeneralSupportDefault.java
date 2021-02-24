package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GeneralSupportDefault<T extends Comparable<T>> implements Context.GeneralSupport<T> {
    private final int populationSize;
    private final int maximumGenerations;
    private final GenomeDefault<T> genesisGenome;
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
    public GenomeDefault<T> createGenesisGenome() {
        return genesisGenome.createCopy();
    }

    @Override
    public float calculateFitness(final GenomeDefault<T> genome) {
        return fitnessCalculator.calculate(genome);
    }
}
