package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.IdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultGeneralSupport implements Context.GeneralSupport {
    private final int populationSize;
    private final IdFactory<String> genomeIdFactory;
    private final GenomeDefaultFactory genomeFactory;
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
    public GenomeDefault createGenesisGenome() {
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
    public float calculateFitness(final GenomeDefault genome) {
        return environment.test(genome);
    }
}
