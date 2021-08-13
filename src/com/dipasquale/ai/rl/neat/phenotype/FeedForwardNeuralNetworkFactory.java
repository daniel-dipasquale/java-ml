/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;
    private final NeuronFactory neuronFactory;

    @Override
    public NeuralNetwork create(final DefaultGenome genome, final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        NeuronPathBuilder neuronPathBuilder = new DefaultNeuronPathBuilder();

        return new DefaultNeuralNetwork(nodes, connections, neuronPathBuilder, neuronFactory);
    }
}
