package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneGroup;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneGroup;

@FunctionalInterface
public interface NeuralNetworkFactory {
    NeuralNetwork create(NodeGeneGroup nodes, ConnectionGeneGroup connections);
}
