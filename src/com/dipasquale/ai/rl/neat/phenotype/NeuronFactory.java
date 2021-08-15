package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGene;

import java.util.Collection;

@FunctionalInterface
public interface NeuronFactory {
    Neuron create(NodeGene node, Collection<InputNeuron> inputIds, Collection<OutputNeuron> outputs);
}
