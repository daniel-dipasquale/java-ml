package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;

public interface NeuronPathBuilder extends Iterable<Neuron> {
    boolean hasNeurons();

    Neuron get(Id neuronId);

    void add(Neuron neuron);

    void addPathLeadingTo(Neuron neuron);

    void clear();
}
