package com.experimental.ai.rl.neat;

import com.experimental.ai.ActivationFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class Neuron<T> {
    @Getter
    private final NodeGene<T> node;
    @Setter
    private float value;

    public float getValue(final ActivationFunction activationFunction) {
        return activationFunction.forward(value + node.getBias());
    }

    public float getValue() {
        return getValue(node.getActivationFunction());
    }

    public void addToValue(final float value) {
        this.value += value;
    }
}
