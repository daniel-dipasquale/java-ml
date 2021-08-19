package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

final class RecurrentNeuronValueMap implements NeuronValueMap {
    private final Map<SequentialId, EnvelopeGroup> neuronValues = new HashMap<>();

    @Override
    public float getValue(final SequentialId id) {
        EnvelopeGroup envelopeGroup = neuronValues.get(id);

        if (envelopeGroup == null) {
            return 0f;
        }

        return envelopeGroup.totalValue;
    }

    @Override
    public void setValue(final SequentialId id, final float value) {
        EnvelopeGroup envelopeGroup = neuronValues.computeIfAbsent(id, k -> new EnvelopeGroup());

        envelopeGroup.clear();
        envelopeGroup.replace(id, value);
    }

    @Override
    public void addToValue(final SequentialId id, final float delta, final SequentialId sourceId) {
        EnvelopeGroup envelopeGroup = neuronValues.computeIfAbsent(id, k -> new EnvelopeGroup());

        envelopeGroup.replace(sourceId, delta);
    }

    @Override
    public void clear() {
        neuronValues.clear();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Envelope {
        private float value;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EnvelopeGroup {
        private final Map<SequentialId, Envelope> values = new HashMap<>();
        private float totalValue = 0f;

        private void replace(final SequentialId id, final float delta) {
            Envelope oldEnvelope = values.replace(id, new Envelope(delta));

            totalValue += delta;

            if (oldEnvelope != null) {
                totalValue -= oldEnvelope.value;
            }
        }

        private void clear() {
            values.clear();
            totalValue = 0f;
        }
    }
}
