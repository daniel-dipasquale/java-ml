package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
    public void setValue(final SequentialId id, final float value) {
        neuronValues.put(id, new Envelope(value));
    }

    @Override
    public void addToValue(final SequentialId id, final float delta, final SequentialId sourceId) {
        neuronValues.computeIfAbsent(id, k -> new Envelope(0f)).value += delta;
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
