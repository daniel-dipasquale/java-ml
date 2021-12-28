package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.common.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GruNeuronStateGroup extends AbstractRecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "GRU_H";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory memory;

    private static float calculateUpdateGate(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(0), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(0), neuron.getBias()};

        return Neuron.calculateValue(SIGMOID_ACTIVATION_FUNCTION, weights, values, biases);
    }

    private static float calculateResetGate(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(1), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(1), neuron.getBias()};

        return Neuron.calculateValue(SIGMOID_ACTIVATION_FUNCTION, weights, values, biases);
    }

    private static float calculateCandidateValue(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(2), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(2), neuron.getBias()};

        return Neuron.calculateValue(TAN_H_ACTIVATION_FUNCTION, weights, values, biases);
    }

    @Override
    protected float calculateRecurrentValue(final Neuron neuron, final NeuronOutputConnection connection) {
        Id neuronId = neuron.getId();
        float previousValue = memory.getValueOrDefault(HIDDEN_DIMENSION, neuronId);
        float currentValue = getValue(neuronId);
        float updateGate = calculateUpdateGate(neuron, connection, previousValue, currentValue);
        float resetGate = calculateResetGate(neuron, connection, previousValue, currentValue);
        float outputValue = (1f - updateGate) * previousValue + updateGate * calculateCandidateValue(neuron, connection, resetGate * previousValue, currentValue);

        return Neuron.calculateValue(neuron.getActivationFunction(), 1f, outputValue, 0f);
    }

    @Override
    public void endCycle(final Id neuronId) {
        memory.setValue(HIDDEN_DIMENSION, neuronId, getValue(neuronId));
    }
}
