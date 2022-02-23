package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NeuronLayerReader {
    private final List<Neuron> neurons;
    private final NeuronStateGroup neuronState;

    public int size() {
        return neurons.size();
    }

    public ActivationFunctionType getType(final int index) {
        Neuron neuron = neurons.get(index);

        return ActivationFunctionType.from(neuron.getActivationFunction());
    }

    public float getValue(final int index) {
        Neuron neuron = neurons.get(index);

        return neuronState.calculateValue(neuron);
    }
}
