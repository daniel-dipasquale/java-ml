package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;

import java.util.Collection;

interface Neuron<T> {
    T getId();

    NodeGeneType getType();

    ActivationFunction getActivationFunction();

    Collection<T> getInputIds();

    Collection<NeuronOutput<T>> getOutputs();

    float getValue(ActivationFunction activationFunction);

    default float getValue() {
        return getValue(getActivationFunction());
    }

    void forceValue(float newValue);

    void addToValue(T id, float delta);

    void reset();
}
