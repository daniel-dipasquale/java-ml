package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;

import java.util.Collection;

public interface Neuron {
    SequentialId getId();

    NodeGeneType getType();

    ActivationFunction getActivationFunction();

    Collection<InputNeuron> getInputs();

    Collection<OutputNeuron> getOutputs();

    float getValue(ActivationFunction activationFunction);

    default float getValue() {
        return getValue(getActivationFunction());
    }

    void setValue(float newValue);

    void addToValue(SequentialId id, float delta);
}
