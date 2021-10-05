package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FeedForwardNeuronValueGroup implements NeuronValueGroup {
    private final Map<SequentialId, Envelope> neuronValues = new HashMap<>();

    @Override
    public float getValue(final SequentialId id) {
        Envelope envelope = neuronValues.get(id);

        if (envelope == null) {
            return 0f;
        }

        return envelope.value;
    }

    @Override
    public float getValue(final SequentialId id, final SequentialId sourceId) {
        return getValue(id);
    }

    @Override
    public void setValue(final SequentialId id, final float value) {
        neuronValues.put(id, new Envelope(value));
    }

    @Override
    public void addToValue(final SequentialId id, final float value, final SequentialId sourceId) {
        neuronValues.computeIfAbsent(id, k -> new Envelope(0f)).value += value;
    }

    @Override
    public void clear() {
        neuronValues.clear();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Envelope {
        private float value;
    }
}
