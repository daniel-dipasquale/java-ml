package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.RecyclableVersion;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

public final class NeuronDefault implements Neuron {
    private final NodeGene node;
    @Getter
    private final Collection<NeuronInput> inputs;
    @Getter
    private final Collection<NeuronOutput> outputs;
    private final RecyclableVersion activationNumber;
    private int valueActivationNumber;
    private float value;

    @Builder(access = AccessLevel.PACKAGE)
    public NeuronDefault(final NodeGene node, final Collection<NeuronInput> inputs, final Collection<NeuronOutput> outputs, final RecyclableVersion activationNumber) {
        this.node = node;
        this.inputs = inputs;
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
    public void setValue(final float newValue) {
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
        return new NeuronRecurrentSingleMemory(node, inputs, outputs, activationNumber);
    }

    @Override
    public String toString() {
        return node.toString();
    }
}

