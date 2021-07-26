package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;

@FunctionalInterface
public interface NeuralNetworkFactory {
    NeuralNetwork create(DefaultGenome genome, NodeGeneMap nodes, ConnectionGeneMap connections);
}
