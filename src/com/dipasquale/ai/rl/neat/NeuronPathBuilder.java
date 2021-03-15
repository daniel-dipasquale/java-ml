package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;

interface NeuronPathBuilder extends Iterable<Neuron> {
    boolean hasNeurons();

    Neuron get(SequentialId neuronId);

    Neuron add(Neuron neuron);

    void addPathLeadingTo(Neuron neuron);

    void clear();
}
