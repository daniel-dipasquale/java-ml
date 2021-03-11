package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.common.CircularVersionInt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronRecurrentSingleMemory implements Neuron {
    private final NodeGene node;
    @Getter
    private final Set<SequentialId> inputIds;
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
            valueOutdated = false;

            value = values.values().stream()
                    .map(v -> v.value)
                    .reduce(0f, Float::sum);
        }

        return activationFunction.forward(value + node.getBias());
    }

    @Override
    public void forceValue(final float newValue) {
        throw new NotImplementedException("cannot force value on recurrent neurons");
    }

    @Override
    public void addToValue(final SequentialId id, final float delta) {
        if (valueActivationNumber != activationNumber.current()) {
            valueActivationNumber = activationNumber.current();
            values.clear();
            value = 0f;
            valueOutdated = false;
        }

        values.computeIfAbsent(id, Value::new).value = delta;
        valueOutdated = true;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Value {
        private final SequentialId id;
        private float value;
    }
}
