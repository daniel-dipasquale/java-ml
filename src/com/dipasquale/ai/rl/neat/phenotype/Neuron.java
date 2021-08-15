package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.ActivationFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;

import java.util.Collection;

public interface Neuron {
    SequentialId getId();

    NodeGeneType getType();

    float getBias();

    ActivationFunction getActivationFunction();

    Collection<InputNeuron> getInputs();

    Collection<OutputNeuron> getOutputs();

    default float getValue(final float value) {
        return getActivationFunction().forward(value + getBias());
    }

    default float getValue(final NeuronValueMap neuronValues) {
        float value = neuronValues.getValue(getId());

        return getValue(value);
    }
}
