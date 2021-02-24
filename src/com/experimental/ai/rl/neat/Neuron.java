package com.experimental.ai.rl.neat;

import com.experimental.ai.common.ActivationFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class Neuron<T> {
    @Getter
    private final NodeGene<T> node;
    @Getter
    private final Collection<T> inputIds;
    @Getter
    private final Collection<Output<T>> outputs;
    private final Map<T, Float> inputValues = new HashMap<>();
    private float value = 0f;

    public float getValue(final ActivationFunction activationFunction) {
        return activationFunction.forward(value + node.getBias());
    }

    public float getValue() {
        return getValue(node.getActivationFunction());
    }

    public void forceValue(final float newValue) {
        value = newValue;
    }

    public void addToValue(final T id, final float delta) {
        if (inputValues.put(id, delta) != null) {
            value = inputValues.values().stream()
                    .reduce(0f, Float::sum);
        } else {
            value += delta;
        }
    }

    public void reset() {
        inputValues.clear();
        value = 0f;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    public static final class Output<T> {
        private final T id;
        private final float weight;
    }
}
