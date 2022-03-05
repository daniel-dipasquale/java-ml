package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GruNeatNeuralNetworkFactory implements NeatNeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -8981717991145980316L;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) GruNeuronStateGroup::new;
    private final NeuronLayerTopologyDefinition outputTopologyDefinition;

    @Override
    public NeatNeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new CyclicNeuronPathBuilder();
        NeatNeuronMemoryFactory neuronMemoryFactory = new NeatNeuronMemoryFactory(genome);

        return new ConcurrentNeatNeuralNetwork(genome, neuronPathBuilder, outputTopologyDefinition, neuronMemoryFactory, NEURON_STATE_FACTORY);
    }
}
