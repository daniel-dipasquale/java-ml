package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.Id;

import java.util.List;

public interface NeuronPathBuilder extends Iterable<Neuron> {
    boolean hasNeurons();

    Neuron get(Id neuronId);

    void add(Neuron neuron);

    void addPathsLeadingTo(List<Neuron> neurons);

    void clear();
}
