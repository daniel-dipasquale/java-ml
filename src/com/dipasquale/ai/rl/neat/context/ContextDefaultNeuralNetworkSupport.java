package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultNeuralNetworkSupport implements Context.NeuralNetworkSupport {
    private final NeuralNetworkFactory factory;

    @Override
    public NeuralNetwork create(final GenomeDefault genome, final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        return factory.create(genome, nodes, connections);
    }
}
