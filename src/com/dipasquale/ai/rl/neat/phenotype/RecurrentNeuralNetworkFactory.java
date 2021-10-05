package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecurrentNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7222558913199103708L;
    private static final NeuronValueGroupFactory NEURON_VALUES_FACTORY = (NeuronValueGroupFactory & Serializable) RecurrentNeuronValueGroup::new;

    @Override
    public NeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new RecurrentNeuronPathBuilder();
        ObjectFactory<NeuronMemory> neuronMemoryFactory = (ObjectFactory<NeuronMemory> & Serializable) () -> new NeuronMemory(genome);

        return new DefaultNeuralNetwork(genome, neuronPathBuilder, neuronMemoryFactory, NEURON_VALUES_FACTORY);
    }
}
