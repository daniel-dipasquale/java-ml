package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronFeedForward implements Neuron {
    private final NodeGene node;
    @Getter
    private final Collection<SequentialId> inputIds;
    @Getter
    private final Collection<NeuronOutput> outputs;
    private float value = 0f;
    private boolean resetValue = false;

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
        try {
            if (activationFunction == null) {
                return node.getActivationFunction().forward(value + node.getBias());
            }

            return activationFunction.forward(value + node.getBias());
        } finally {
            resetValue = true;
        }
    }

    @Override
    public void forceValue(final float newValue) {
        value = newValue;
        resetValue = false;
    }

    @Override
    public void addToValue(final SequentialId id, final float delta) {
        if (resetValue) {
            value = 0f;
            resetValue = false;
        }

        value += delta;
    }
}
