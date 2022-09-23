package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
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
    private final NodeGene nodeGene;
    @Getter
    private final Collection<NeuronInputConnection> inputConnections;
    @Getter
    private final Collection<NeuronOutputConnection> outputConnections;

    public Id getId() {
        return nodeGene.getId();
    }

    public NodeGeneType getType() {
        return nodeGene.getType();
    }

    public float getBias() {
        return nodeGene.getBias();
    }

    public float getRecurrentBias(final int index) {
        return nodeGene.getRecurrentBiases().get(index);
    }

    public ActivationFunction getActivationFunction() {
        return nodeGene.getActivationFunction();
    }

    @Override
    public String toString() {
        return nodeGene.toString();
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

