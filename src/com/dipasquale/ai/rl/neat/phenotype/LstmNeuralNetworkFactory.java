package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LstmNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -4644077283351510002L;
    private static final NeuronStateGroupFactory NEURON_STATE_FACTORY = (NeuronStateGroupFactory & Serializable) LstmNeuronStateGroup::new;
    private final NeuronLayerTopologyDefinition outputTopologyDefinition;

    @Override
    public NeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new CyclicNeuronPathBuilder();
        NeuronMemoryFactory neuronMemoryFactory = new NeuronMemoryFactory(genome);

        return new ConcurrentNeuralNetwork(genome, neuronPathBuilder, outputTopologyDefinition, neuronMemoryFactory, NEURON_STATE_FACTORY);
    }
}
