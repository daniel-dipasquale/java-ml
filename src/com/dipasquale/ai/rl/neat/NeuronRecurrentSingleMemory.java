package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.common.CircularVersionInt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronRecurrentSingleMemory implements Neuron {
    private final NodeGene node;
    @Getter
    private final Collection<NeuronInput> inputs;
    @Getter
    private final Collection<NeuronOutput> outputs;
    private final CircularVersionInt activationNumber;
    private int valueActivationNumber = -1;
    private final Map<SequentialId, Value> values = new HashMap<>();
    private float value = 0f;
    private boolean valueOutdated = false;

    @Override
    public SequentialId getId() {
        return node.getId();
    }

    @Override
    public NodeGeneType getType() {
        return node.getType();
    }

    @Override
    public ActivationFunction getActivationFunction() {
        return node.getActivationFunction();
    }

    @Override
    public float getValue(final ActivationFunction activationFunction) {
        if (valueOutdated) {
            value = values.values().stream()
                    .map(v -> v.value)
                    .reduce(0f, Float::sum);

            valueOutdated = false;
        }

        return activationFunction.forward(value + node.getBias());
    }

    @Override
    public void setValue(final float newValue) {
        values.computeIfAbsent(node.getId(), k -> new Value()).value = newValue;
        value = newValue;
        valueOutdated = false;
    }

    @Override
    public void addToValue(final SequentialId id, final float delta) {
        if (valueActivationNumber != activationNumber.current()) {
            valueActivationNumber = activationNumber.current();
            values.clear();
            value = 0f;
        }

        values.computeIfAbsent(id, k -> new Value()).value = delta;
        valueOutdated = true;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Value {
        private float value;
    }

    @Override
    public String toString() {
        return node.toString();
    }
}
