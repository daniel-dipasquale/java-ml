package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RecurrentNeuronStateGroup extends AbstractRecurrentNeuronStateGroup {
    private static final String HIDDEN_DIMENSION = "VANILLA_H";
    private final NeatNeuronMemory neuronMemory;

    @Override
    protected float calculateRecurrentValue(final Neuron neuron, final NeuronOutputConnection connection) {
        Id neuronId = neuron.getId();
        float[] weights = {connection.getRecurrentWeight(0), connection.getWeight()};
        float[] values = {neuronMemory.getValueOrDefault(HIDDEN_DIMENSION, neuronId), getValue(neuronId)};
        float[] biases = {neuron.getRecurrentBias(0), neuron.getBias()};

        return Neuron.calculateValue(neuron.getActivationFunction(), weights, values, biases);
    }

    @Override
    public void endCycle(final Id neuronId) {
        neuronMemory.setValue(HIDDEN_DIMENSION, neuronId, getValue(neuronId));
    }
}
