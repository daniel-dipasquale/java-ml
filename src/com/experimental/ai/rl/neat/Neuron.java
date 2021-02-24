package com.experimental.ai.rl.neat;

import com.experimental.ai.common.ActivationFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

interface Neuron<T> {
    T getId();

    NodeGene.Type getType();

    ActivationFunction getActivationFunction();

    Collection<T> getInputIds();

    Collection<Output<T>> getOutputs();

    float getValue(ActivationFunction activationFunction);

    default float getValue() {
        return getValue(getActivationFunction());
    }

    void forceValue(float newValue);

    void addToValue(T id, float delta);

    void reset();

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    final class Output<T> {
        private final T neuronId;
        private final float connectionWeight;
    }
}
