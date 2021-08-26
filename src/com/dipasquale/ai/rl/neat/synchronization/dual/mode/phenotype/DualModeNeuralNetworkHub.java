package com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneGroup;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneGroup;
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
public final class DualModeNeuralNetworkHub implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8461491541333740085L;
    private final NeuralNetworkFactory factory;
    private final DualModeMap<String, Activator> activators;

    public DualModeNeuralNetworkHub(final boolean concurrent, final int numberOfThreads, final DualModeNeuralNetworkHub neuralNetworkHub) {
        this(neuralNetworkHub.factory, new DualModeMap<>(concurrent, numberOfThreads, neuralNetworkHub.activators));
    }

    public DualModeNeuralNetworkHub(final boolean concurrent, final int numberOfThreads, final NeuralNetworkFactory factory) {
        this(factory, new DualModeMap<>(concurrent, numberOfThreads));
    }

    private Activator getOrCreateActivator(final Activator activator, final DefaultGenome genome, final NodeGeneGroup nodes, final ConnectionGeneGroup connections, final PopulationState populationState) {
        if (activator != null && activator.genome == genome) {
            return activator;
        }

        return new Activator(genome, populationState, factory.create(nodes, connections));
    }

    public Genome getOrCreateProxyGenome(final DefaultGenome genome, final NodeGeneGroup nodes, final ConnectionGeneGroup connections, final PopulationState populationState) {
        return activators.compute(genome.getId(), (gid, oa) -> getOrCreateActivator(oa, genome, nodes, connections, populationState));
    }

    public NeuralNetwork getPhenotype(final DefaultGenome genome) {
        return activators.get(genome.getId()).neuralNetwork;
    }

    @Override
    public void switchMode(final boolean concurrent) {
        activators.switchMode(concurrent);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Activator implements Genome {
        private final DefaultGenome genome;
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
