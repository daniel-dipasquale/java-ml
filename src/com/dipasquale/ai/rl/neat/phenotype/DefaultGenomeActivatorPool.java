package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultGenomeActivatorPool implements GenomeActivatorPool {
    private final Map<String, GenomeActivator> genomeActivators;
    private final NeuralNetworkFactory neuralNetworkFactory;

    private GenomeActivator getOrCreate(final GenomeActivator oldGenomeActivator, final Genome genome, final PopulationState populationState) {
        if (oldGenomeActivator != null && oldGenomeActivator.isOwnedBy(genome)) {
            return oldGenomeActivator;
        }

        return new GenomeActivator(genome, populationState, neuralNetworkFactory.create(genome));
    }

    @Override
    public GenomeActivator getOrCreate(final Genome genome, final PopulationState populationState) {
        return genomeActivators.compute(genome.getId(), (gid, oga) -> getOrCreate(oga, genome, populationState));
    }
}
