package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FeedForwardNeuronStateGroup implements NeuronStateGroup {
    private final Map<Id, NeuronState> states = new HashMap<>();

    private float getValue(final Id neuronId) {
        NeuronState state = states.get(neuronId);

        if (state == null) {
            return 0f;
        }

        return state.value;
    }

    @Override
    public float calculateValue(final Neuron neuron) {
        float value = getValue(neuron.getId());

        return neuron.calculateValue(value);
    }

    @Override
    public float calculateValue(final Neuron neuron, final NeuronOutputConnection connection) {
        float value = getValue(neuron.getId());

        return neuron.calculateValue(connection, value);
    }

    @Override
    public void setValue(final Id neuronId, final float value) {
        states.put(neuronId, new NeuronState(value));
    }

    @Override
    public void addValue(final Id neuronId, final float value, final Id sourceNeuronId) {
        NeuronState state = states.computeIfAbsent(neuronId, __ -> new NeuronState(0f));

        state.value += value;
    }

    @Override
    public void endCycle(final Id neuronId) {
    }

    @Override
    public void clear() {
        states.clear();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NeuronState {
        private float value;
    }
}
