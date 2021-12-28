package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;

interface NeuronStateGroup {
    float calculateValue(Neuron neuron);

    float calculateValue(Neuron neuron, NeuronOutputConnection connection);

    void setValue(Id neuronId, float value);

    void addValue(Id neuronId, float value, Id sourceNeuronId);

    void endCycle(Id neuronId);

    void clear();
}
