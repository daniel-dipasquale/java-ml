package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;
    private static final ObjectFactory<NeuronMemory> NEURON_MEMORY_FACTORY = (ObjectFactory<NeuronMemory> & Serializable) () -> null;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) nm -> new FeedForwardNeuronStateGroup();

    @Override
    public NeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new AcyclicNeuronPathBuilder();

        return new ConcurrentNeuralNetwork(genome, neuronPathBuilder, NEURON_MEMORY_FACTORY, NEURON_STATE_FACTORY);
    }
}
