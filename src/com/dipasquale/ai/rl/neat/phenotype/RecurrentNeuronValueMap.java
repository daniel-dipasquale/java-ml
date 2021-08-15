package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

final class RecurrentNeuronValueMap implements NeuronValueMap {
    private final Map<SequentialId, EnvelopeTable> table = new HashMap<>();

    @Override
    public float getValue(final SequentialId id) {
        EnvelopeTable envelopes = table.get(id);

        if (envelopes == null) {
            return 0f;
        }

        return envelopes.total;
    }

    @Override
    public void setValue(final SequentialId id, final float value) {
        EnvelopeTable envelopes = table.computeIfAbsent(id, k -> new EnvelopeTable());

        envelopes.replace(id, value);
    }

    @Override
    public void addToValue(final SequentialId id, final SequentialId fromId, final float delta) {
        EnvelopeTable envelopes = table.computeIfAbsent(id, k -> new EnvelopeTable());

        envelopes.replace(fromId, delta);
    }

    @Override
    public void clear() {
        table.clear();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Envelope {
        private float value;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EnvelopeTable {
        private final Map<SequentialId, Envelope> values = new HashMap<>();
        private float total = 0f;

        private void replace(final SequentialId id, final float delta) {
            Envelope oldDelta = values.replace(id, new Envelope(delta));

            total += delta;

            if (oldDelta != null) {
                total -= oldDelta.value;
            }
        }
    }
}
