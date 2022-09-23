package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.data.structure.collection.IterableArray;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GenomeActivatorPool implements Serializable {
    @Serial
    private static final long serialVersionUID = 6724662215050116412L;
    private final IterableArray<GenomeActivator> genomeActivators;
    private final NeatNeuralNetworkFactory neuralNetworkFactory;

    private GenomeActivator createGenomeActivator(final Genome genome, final PopulationState populationState) {
        return new GenomeActivator(genome, populationState, neuralNetworkFactory.create(genome));
    }

    private GenomeActivator provide(final GenomeActivator oldGenomeActivator, final Genome genome, final PopulationState populationState) {
        if (oldGenomeActivator != null && oldGenomeActivator.isOwnedBy(genome)) {
            return oldGenomeActivator;
        }

        return createGenomeActivator(genome, populationState);
    }

    public GenomeActivator provide(final Genome genome, final PopulationState populationState) {
        return genomeActivators.compute(genome.getId(), oldGenomeActivator -> provide(oldGenomeActivator, genome, populationState));
    }

    public GenomeActivator create(final Genome genome, final PopulationState populationState) {
        return createGenomeActivator(genome, populationState.createClone());
    }
}
