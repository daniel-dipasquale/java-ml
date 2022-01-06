package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecurrentNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7222558913199103708L;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) RecurrentNeuronStateGroup::new;
    private final NeuronLayerNormalizer outputLayerNormalizer;

    @Override
    public NeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new CyclicNeuronPathBuilder();
        NeuronMemoryFactory neuronMemoryFactory = new NeuronMemoryFactory(genome);

        return new ConcurrentNeuralNetwork(genome, neuronPathBuilder, outputLayerNormalizer, neuronMemoryFactory, NEURON_STATE_FACTORY);
    }
}
