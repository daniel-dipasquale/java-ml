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
    public void addToValue(final SequentialId id, final float delta) {
        value += delta;
    }

    @Override
    public void reset() {
        value = 0f;
    }
}
