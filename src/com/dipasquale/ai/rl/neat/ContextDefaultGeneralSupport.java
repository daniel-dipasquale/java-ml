package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.common.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultGeneralSupport<T extends Comparable<T>> implements Context.GeneralSupport<T> {
    private final int populationSize;
    private final SequentialIdFactory<T> genomeIdFactory;
    private final ObjectFactory<GenomeDefault<T>> genesisGenomeFactory;
    private final SequentialIdFactory<T> speciesIdFactory;
    private final FitnessDeterminer.Factory fitnessDeterminerFactory;
    private final FitnessCalculator fitnessCalculator;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public T createGenomeId() {
        return genomeIdFactory.next();
    }

    @Override
    public GenomeDefault<T> createGenesisGenome(final int generation) {
        if (generation == 0) {
            return null; // TODO: finish
        }

        return genesisGenomeFactory.create();
    }

    @Override
    public T createSpeciesId() {
        return speciesIdFactory.next();
    }

    @Override
    public FitnessDeterminer createFitnessDeterminer() {
        return fitnessDeterminerFactory.create();
    }

    @Override
    public float calculateFitness(final GenomeDefault<T> genome) {
        return fitnessCalculator.calculate(genome);
    }
}
