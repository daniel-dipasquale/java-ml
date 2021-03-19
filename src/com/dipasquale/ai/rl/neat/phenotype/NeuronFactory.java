package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.common.CircularVersionInt;

import java.util.Collection;

@FunctionalInterface
public interface NeuronFactory {
    Neuron create(NodeGene node, Collection<NeuronInput> inputIds, Collection<NeuronOutput> outputs, CircularVersionInt activationNumber);
}
