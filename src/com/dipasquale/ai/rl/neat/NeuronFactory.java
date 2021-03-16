package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.CircularVersionInt;

import java.util.Collection;

@FunctionalInterface
interface NeuronFactory {
    Neuron create(NodeGene node, Collection<NeuronInput> inputIds, Collection<NeuronOutput> outputs, CircularVersionInt activationNumber);
}
