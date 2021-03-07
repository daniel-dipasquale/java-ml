package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;

import java.util.Collection;

interface Neuron {
    SequentialId getId();

    NodeGeneType getType();

    ActivationFunction getActivationFunction();

    Collection<SequentialId> getInputIds();

    Collection<NeuronOutput> getOutputs();

    float getValue(ActivationFunction activationFunction);

    default float getValue() {
        return getValue(getActivationFunction());
    }

    void forceValue(float newValue);

    void addToValue(SequentialId id, float delta);

    void reset();
}
