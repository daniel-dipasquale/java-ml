package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Deque;

@RequiredArgsConstructor
public final class ContextDefaultGeneralSupport implements Context.GeneralSupport {
    @Serial
    private static final long serialVersionUID = -6562774113135100248L;
    private final int populationSize;
    private final SequentialIdFactory genomeIdFactory;
    private final GenomeDefaultFactory genomeFactory;
    private final SequentialIdFactory speciesIdFactory;
    private final FitnessDeterminerFactory fitnessDeterminerFactory;
    private final NeatEnvironment environment;
    private final Deque<String> genomeIdsKilled;

    @Override
    public int populationSize() {
        return populationSize;
    }

    @Override
    public String createGenomeId() {
        String id = genomeIdsKilled.pollFirst();

        if (id != null) {
            return id;
        }

        return genomeIdFactory.create().toString();
    }

    @Override
    public GenomeDefault createGenesisGenome(final Context context) {
        return genomeFactory.create(context);
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.create().toString();
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
    public void markToKill(final GenomeDefault genome) {
        genomeIdsKilled.add(genome.getId());
    }

    @Override
    public int getGenomesKilledCount() {
        return genomeIdsKilled.size();
    }

    @Override
    public void reset() {
        genomeIdFactory.reset();
        speciesIdFactory.reset();
        genomeIdsKilled.clear();
    }
}
