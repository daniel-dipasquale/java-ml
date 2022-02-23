package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
final class Neuron implements Serializable {
    @Serial
    private static final long serialVersionUID = -7305330143842774982L;
    private final NodeGene node;
    @Getter
    private final Collection<NeuronInputConnection> inputConnections;
    @Getter
    private final Collection<NeuronOutputConnection> outputConnections;

    public Id getId() {
        return node.getId();
    }

    public NodeGeneType getType() {
        return node.getType();
    }

    public float getBias() {
        return node.getBias();
    }

    public float getRecurrentBias(final int index) {
        return node.getRecurrentBiases().get(index);
    }

    public ActivationFunction getActivationFunction() {
        return node.getActivationFunction();
    }

    @Override
    public String toString() {
        return node.toString();
    }

    public static float calculateValue(final ActivationFunction activationFunction, final float[] weights, final float[] values, final float[] biases) {
        float sum = 0f;

        for (int i = 0; i < values.length; i++) {
            float value = weights[i] * values[i] + biases[i];

            sum += value;
        }

        return activationFunction.forward(sum);
    }

    public static float calculateValue(final ActivationFunction activationFunction, final float weight, final float value, final float bias) {
        return activationFunction.forward(weight * value + bias);
    }

    public float calculateValue(final float weight, final float value) {
        return calculateValue(getActivationFunction(), weight, value, getBias());
    }

    public float calculateValue(final NeuronOutputConnection connection, final float value) {
        if (connection == null) {
            return calculateValue(1f, value);
        }

        return calculateValue(connection.getWeight(), value);
    }

    public float calculateValue(final float value) {
        return calculateValue(1f, value);
    }
}

