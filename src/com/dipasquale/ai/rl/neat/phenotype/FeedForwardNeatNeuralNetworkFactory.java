package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeatNeuralNetworkFactory implements NeatNeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;
    private static final ObjectFactory<NeatNeuronMemory> NEURON_MEMORY_FACTORY = (ObjectFactory<NeatNeuronMemory> & Serializable) () -> null;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) __ -> new FeedForwardNeuronStateGroup();
    private final NeuronLayerTopologyDefinition outputTopologyDefinition;

    @Override
    public NeatNeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new AcyclicNeuronPathBuilder();

        return new ConcurrentNeatNeuralNetwork(genome, neuronPathBuilder, outputTopologyDefinition, NEURON_MEMORY_FACTORY, NEURON_STATE_FACTORY);
    }
}
