package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneGroup;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneGroup;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;

    @Override
    public NeuralNetwork create(final NodeGeneGroup nodes, final ConnectionGeneGroup connections) {
        NeuronPathBuilder neuronPathBuilder = new FeedForwardNeuronPathBuilder();
        ObjectFactory<NeuronValueGroup> neuronValuesFactory = (ObjectFactory<NeuronValueGroup> & Serializable) FeedForwardNeuronValueGroup::new;

        return new DefaultNeuralNetwork(nodes, connections, neuronPathBuilder, neuronValuesFactory);
    }
}
