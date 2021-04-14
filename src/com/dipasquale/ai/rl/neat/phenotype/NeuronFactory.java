package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.common.CyclicVersion;

import java.io.Serializable;
import java.util.Collection;

@FunctionalInterface
public interface NeuronFactory extends Serializable {
    Neuron create(NodeGene node, Collection<NeuronInput> inputIds, Collection<NeuronOutput> outputs, CyclicVersion activationNumber);
}
