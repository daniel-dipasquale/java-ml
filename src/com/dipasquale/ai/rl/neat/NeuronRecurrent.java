package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronRecurrent<T> implements Neuron<T> {
    private final NodeGene<T> node;
    @Getter
    private final Collection<T> inputIds;
    @Getter
    private final Collection<Neuron.Output<T>> outputs;
    private final Map<T, Float> inputValues = new HashMap<>();
    private float value = 0f;

    @Override
    public T getId() {
        return node.getId();
    }

    @Override
    public NodeGene.Type getType() {
        return node.getType();
    }

    @Override
    public ActivationFunction getActivationFunction() {
        return node.getActivationFunction();
    }

    @Override
    public float getValue(final ActivationFunction activationFunction) {
        if (activationFunction == null) {
            return node.getActivationFunction().forward(value + node.getBias());
        }

        return activationFunction.forward(value + node.getBias());
    }

    @Override
    public void forceValue(final float newValue) {
        value = newValue;
    }

    @Override
    public void addToValue(final T id, final float delta) {
        if (inputValues.put(id, delta) != null) {
            value = inputValues.values().stream()
                    .reduce(0f, Float::sum);
        } else {
            value += delta;
        }
    }

    @Override
    public void reset() {
        inputValues.clear();
        value = 0f;
    }
}
