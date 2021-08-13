/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;

public interface NeuronPathBuilder extends Iterable<Neuron> {
    boolean hasNeurons();

    Neuron get(SequentialId neuronId);

    Neuron add(Neuron neuron);

    void addPathLeadingTo(Neuron neuron);

    void clear();
}
