package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecurrentNeatNeuralNetworkFactory implements NeatNeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7222558913199103708L;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) RecurrentNeuronStateGroup::new;
    private final NeuronLayerTopologyDefinition outputTopologyDefinition;

    @Override
    public NeatNeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new CyclicNeuronPathBuilder();
        NeatNeuronMemoryFactory neuronMemoryFactory = new NeatNeuronMemoryFactory(genome);

        return new ConcurrentNeatNeuralNetwork(genome, neuronPathBuilder, outputTopologyDefinition, neuronMemoryFactory, NEURON_STATE_FACTORY);
    }
}
