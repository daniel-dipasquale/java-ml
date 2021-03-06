package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultGeneralSupport<T extends Comparable<T>> implements Context.GeneralSupport<T> {
    private final int populationSize;
    private final IdFactory<String> genomeIdFactory;
    private final GenomeDefaultFactory<T> genomeFactory;
    private final IdFactory<String> speciesIdFactory;
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final Environment environment;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public String createGenomeId() {
        return genomeIdFactory.createId();
    }

    @Override
    public GenomeDefault<T> createGenesisGenome() {
        return genomeFactory.create();
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.createId();
    }

    @Override
    public FitnessDeterminer createFitnessDeterminer() {
        return fitnessDeterminerFactory.create();
    }

    @Override
    public float calculateFitness(final GenomeDefault<T> genome) {
        return environment.test(genome);
    }
}
