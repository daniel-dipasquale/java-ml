package com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivatorPool;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.StateStrategyGenomeActivatorPool;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

public final class DualModeGenomeActivatorPool implements GenomeActivatorPool, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3956562667609505308L;
    private final DualModeMap<String, GenomeActivator, DualModeMapFactory> genomeActivators;
    private final NeatNeuralNetworkFactory neuralNetworkFactory;
    private transient StateStrategyGenomeActivatorPool genomeActivatorPool;

    public DualModeGenomeActivatorPool(final DualModeMap<String, GenomeActivator, DualModeMapFactory> genomeActivators, final NeatNeuralNetworkFactory neuralNetworkFactory) {
        this.genomeActivators = genomeActivators;
        this.neuralNetworkFactory = neuralNetworkFactory;
        this.genomeActivatorPool = new StateStrategyGenomeActivatorPool(genomeActivators, neuralNetworkFactory);
    }

    @Override
    public GenomeActivator provide(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.provide(genome, populationState);
    }

    @Override
    public GenomeActivator create(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.create(genome, populationState);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        genomeActivators.activateMode(concurrencyLevel);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        genomeActivatorPool = new StateStrategyGenomeActivatorPool(genomeActivators, neuralNetworkFactory);
    }
}
