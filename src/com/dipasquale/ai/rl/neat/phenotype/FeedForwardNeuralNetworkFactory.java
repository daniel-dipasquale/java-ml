package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;
    private final NeuronFactory neuronFactory;

    @Override
    public NeuralNetwork create(final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        NeuronPathBuilder neuronPathBuilder = new DefaultNeuronPathBuilder();
        ObjectFactory<NeuronValueMap> neuronValuesFactory = DefaultNeuronValueMap::new;

        return new DefaultNeuralNetwork(nodes, connections, neuronPathBuilder, neuronFactory, neuronValuesFactory);
    }
}
