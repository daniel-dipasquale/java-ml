package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.SigmoidActivationFunction;
import com.dipasquale.ai.rl.neat.function.activation.TanHActivationFunction;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LstmNeuronStateGroup extends AbstractRecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "LSTM_H";
    private static final String CELL_DIMENSION = "LSTM_C";
    private static final SigmoidActivationFunction SIGMOID_ACTIVATION_FUNCTION = SigmoidActivationFunction.getInstance();
    private static final TanHActivationFunction TAN_H_ACTIVATION_FUNCTION = TanHActivationFunction.getInstance();
    private final NeuronMemory neuronMemory;
    private final Map<Id, Float> cellValues = new HashMap<>();

    private static float calculateForgetGate(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(0), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(0), neuron.getBias()};

        return Neuron.calculateValue(SIGMOID_ACTIVATION_FUNCTION, weights, values, biases);
    }

    private static float calculateInputGate(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(1), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(1), neuron.getBias()};

        return Neuron.calculateValue(SIGMOID_ACTIVATION_FUNCTION, weights, values, biases);
    }

    private static float calculateCandidateCellValue(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(2), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(2), neuron.getBias()};

        return Neuron.calculateValue(TAN_H_ACTIVATION_FUNCTION, weights, values, biases);
    }

    private static float calculateOutputValue(final Neuron neuron, final NeuronOutputConnection connection, final float previousValue, final float currentValue) {
        float[] weights = {connection.getRecurrentWeight(3), connection.getWeight()};
        float[] values = {previousValue, currentValue};
        float[] biases = {neuron.getRecurrentBias(3), neuron.getBias()};

        return Neuron.calculateValue(SIGMOID_ACTIVATION_FUNCTION, weights, values, biases);
    }

    @Override
    protected float calculateRecurrentValue(final Neuron neuron, final NeuronOutputConnection connection) {
        Id neuronId = neuron.getId();
        float previousCellValue = neuronMemory.getValueOrDefault(CELL_DIMENSION, neuronId);
        float previousValue = neuronMemory.getValueOrDefault(HIDDEN_DIMENSION, neuronId);
        float currentValue = getValue(neuronId);
        float forgetGate = calculateForgetGate(neuron, connection, previousValue, currentValue);
        float inputGate = calculateInputGate(neuron, connection, previousValue, currentValue);
        float currentCellValue = forgetGate * previousCellValue + inputGate * calculateCandidateCellValue(neuron, connection, previousValue, currentValue);
        float outputValue = calculateOutputValue(neuron, connection, previousValue, currentValue) * TAN_H_ACTIVATION_FUNCTION.forward(currentCellValue);
        ActivationFunction activationFunction = neuron.getActivationFunction();

        cellValues.put(neuronId, currentCellValue);

        if (activationFunction == TAN_H_ACTIVATION_FUNCTION) {
            return outputValue;
        }

        return Neuron.calculateValue(activationFunction, 1f, outputValue, 0f);
    }

    @Override
    public void endCycle(final Id neuronId) {
        neuronMemory.setValue(HIDDEN_DIMENSION, neuronId, getValue(neuronId));
        neuronMemory.setValue(CELL_DIMENSION, neuronId, Objects.requireNonNullElse(cellValues.remove(neuronId), 0f));
    }
}
