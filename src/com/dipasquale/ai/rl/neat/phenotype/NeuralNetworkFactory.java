package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;

import java.io.Serializable;

@FunctionalInterface
public interface NeuralNetworkFactory extends Serializable {
    NeuralNetwork create(GenomeDefault genome, NodeGeneMap nodes, ConnectionGeneMap connections);
}
