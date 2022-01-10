package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class StateStrategyGenomeActivatorPool implements GenomeActivatorPool {
    private final Map<String, GenomeActivator> genomeActivators;
    private final NeuralNetworkFactory neuralNetworkFactory;

    private GenomeActivator createGenomeActivator(final Genome genome, final PopulationState populationState) {
        return new GenomeActivator(genome, populationState, neuralNetworkFactory.create(genome));
    }

    private GenomeActivator getOrCreate(final GenomeActivator oldGenomeActivator, final Genome genome, final PopulationState populationState) {
        if (oldGenomeActivator != null && oldGenomeActivator.isOwnedBy(genome)) {
            return oldGenomeActivator;
        }

        return createGenomeActivator(genome, populationState);
    }

    @Override
    public GenomeActivator provide(final Genome genome, final PopulationState populationState) {
        return genomeActivators.compute(genome.getId(), (__, oldGenomeActivator) -> getOrCreate(oldGenomeActivator, genome, populationState));
    }

    @Override
    public GenomeActivator create(final Genome genome, final PopulationState populationState) {
        return createGenomeActivator(genome, populationState.createClone());
    }
}
