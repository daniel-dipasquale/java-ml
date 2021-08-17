package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecurrentNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7222558913199103708L;

    @Override
    public NeuralNetwork create(final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        NeuronPathBuilder neuronPathBuilder = new RecurrentNeuronPathBuilder();
        ObjectFactory<NeuronValueMap> neuronValuesFactory = RecurrentNeuronValueMap::new;

        return new DefaultNeuralNetwork(nodes, connections, neuronPathBuilder, neuronValuesFactory);
    }
}
