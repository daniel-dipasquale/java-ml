package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.Environment;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.common.IdFactory;
import lombok.RequiredArgsConstructor;

import java.util.Deque;

@RequiredArgsConstructor
public final class ContextDefaultGeneralSupport implements Context.GeneralSupport {
    private final int populationSize;
    private final IdFactory<String> genomeIdFactory;
    private final GenomeDefaultFactory genomeFactory;
    private final IdFactory<String> speciesIdFactory;
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final Environment environment;
    private final Deque<String> genomeIdsDiscarded;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public String createGenomeId() {
        String id = genomeIdsDiscarded.pollFirst();

        if (id != null) {
            return id;
        }

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

    @Override
    public void discardGenome(final GenomeDefault genome) {
        genomeIdsDiscarded.add(genome.getId());
    }
}
