package com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.DefaultGenomeActivatorPool;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivatorPool;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
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
    private final NeuralNetworkFactory neuralNetworkFactory;
    private transient DefaultGenomeActivatorPool genomeActivatorPool;

    private DualModeGenomeActivatorPool(final DualModeMap<String, GenomeActivator, DualModeMapFactory> genomeActivators, final NeuralNetworkFactory neuralNetworkFactory) {
        this.genomeActivators = genomeActivators;
        this.neuralNetworkFactory = neuralNetworkFactory;
        this.genomeActivatorPool = new DefaultGenomeActivatorPool(genomeActivators, neuralNetworkFactory);
    }

    public DualModeGenomeActivatorPool(final DualModeMapFactory mapFactory, final NeuralNetworkFactory neuralNetworkFactory) {
        this(new DualModeMap<>(mapFactory), neuralNetworkFactory);
    }

    @Override
    public GenomeActivator getOrCreate(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.getOrCreate(genome, populationState);
    }

    @Override
    public int concurrencyLevel() {
        return genomeActivators.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        genomeActivators.activateMode(concurrencyLevel);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        genomeActivatorPool = new DefaultGenomeActivatorPool(genomeActivators, neuralNetworkFactory);
    }
}
