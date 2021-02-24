package com.experimental.ai.rl.neat;

import com.experimental.ai.common.ActivationFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronAcyclic<T> implements Neuron<T> {
    private final NodeGene<T> node;
    @Getter
    private final Collection<T> inputIds;
    @Getter
    private final Collection<Neuron.Output<T>> outputs;
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
    public float getValue(final ActivationFunction activationFunction) {
        if (activationFunction == null) {
            return node.getActivationFunction().forward(value + node.getBias());
        }

        return activationFunction.forward(value + node.getBias());
    }

    @Override
    public float getValue() {
        return getValue(node.getActivationFunction());
    }

    @Override
    public void forceValue(final float newValue) {
        value = newValue;
    }

    @Override
    public void addToValue(final T id, final float delta) {
        value += delta;
    }

    @Override
    public void reset() {
        value = 0f;
    }
}
