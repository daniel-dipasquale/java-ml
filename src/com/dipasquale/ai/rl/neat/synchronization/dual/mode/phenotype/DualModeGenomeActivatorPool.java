package com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeGenomeActivatorPool implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8461491541333740085L;
    private final NeuralNetworkFactory neuralNetworkFactory;
    private final DualModeMap<String, DefaultGenomeActivator> genomeActivators;

    public DualModeGenomeActivatorPool(final boolean concurrent, final int numberOfThreads, final DualModeGenomeActivatorPool genomeActivatorPool) {
        this(genomeActivatorPool.neuralNetworkFactory, new DualModeMap<>(concurrent, numberOfThreads, genomeActivatorPool.genomeActivators));
    }

    public DualModeGenomeActivatorPool(final boolean concurrent, final int numberOfThreads, final NeuralNetworkFactory neuralNetworkFactory) {
        this(neuralNetworkFactory, new DualModeMap<>(concurrent, numberOfThreads));
    }

    private DefaultGenomeActivator getOrCreate(final DefaultGenomeActivator oldGenomeActivator, final Genome genome, final PopulationState populationState) {
        if (oldGenomeActivator != null && oldGenomeActivator.genome == genome) {
            return oldGenomeActivator;
        }

        return new DefaultGenomeActivator(genome, populationState, neuralNetworkFactory.create(genome));
    }

    public GenomeActivator getOrCreate(final Genome genome, final PopulationState populationState) {
        return genomeActivators.compute(genome.getId(), (gid, oga) -> getOrCreate(oga, genome, populationState));
    }

    @Override
    public void switchMode(final boolean concurrent) {
        genomeActivators.switchMode(concurrent);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultGenomeActivator implements GenomeActivator, NeuralNetwork, Serializable {
        @Serial
        private static final long serialVersionUID = 7394538848509180506L;
        private final Genome genome;
        private final PopulationState populationState;
        private final NeuralNetwork neuralNetwork;

        @Override
        public String getId() {
            return genome.getId();
        }

        @Override
        public int getGeneration() {
            return populationState.getGeneration();
        }

        @Override
        public int getComplexity() {
            return genome.getComplexity();
        }

        @Override
        public float[] activate(final float[] input) {
            return neuralNetwork.activate(input);
        }
    }
}
