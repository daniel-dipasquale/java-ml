package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.common.CircularVersionInt;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

final class NeuronDefault implements Neuron {
    private final NodeGene node;
    @Getter
    private final Set<SequentialId> inputIds;
    @Getter
    private final Collection<NeuronOutput> outputs;
    private final CircularVersionInt activationNumber;
    private int valueActivationNumber;
    private float value;

    @Builder(access = AccessLevel.PACKAGE)
    NeuronDefault(final NodeGene node, final Set<SequentialId> inputIds, final Collection<NeuronOutput> outputs, final CircularVersionInt activationNumber) {
        this.node = node;
        this.inputIds = inputIds;
        this.outputs = outputs;
        this.activationNumber = activationNumber;
        this.valueActivationNumber = -1;
        this.value = 0f;
    }

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
        return activationFunction.forward(value + node.getBias());
    }

    @Override
    public void forceValue(final float newValue) {
        valueActivationNumber = activationNumber.current();
        value = newValue;
    }

    @Override
    public void addToValue(final SequentialId id, final float delta) {
        if (valueActivationNumber != activationNumber.current()) {
            valueActivationNumber = activationNumber.current();
            value = 0f;
        }

        value += delta;
    }

    public Neuron createRecurrentSingleMemory() {
        return new NeuronRecurrentSingleMemory(node, inputIds, outputs, activationNumber);
    }
}

