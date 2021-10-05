package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RecurrentNeuronValueGroup implements NeuronValueGroup {
    private final Map<SequentialId, ValueGroup> neuronValues = new HashMap<>();
    private final NeuronMemory neuronMemory;

    private float getValueFromNeurons(final SequentialId id) {
        ValueGroup values = neuronValues.get(id);

        if (values == null) {
            return 0f;
        }

        return values.total;
    }

    private float getValueFromMemory(final SequentialId id) {
        if (neuronMemory != null) {
            Float value = neuronMemory.getValue(id);

            if (value != null) {
                return value;
            }
        }

        return 0f;
    }

    @Override
    public float getValue(final SequentialId id) {
        return getValueFromNeurons(id);
    }

    @Override
    public float getValue(final SequentialId id, final SequentialId sourceId) {
        if (!ConnectionGene.isRecurrent(sourceId, id)) {
            return getValue(id);
        }

        return getValue(id) + getValueFromMemory(id);
    }

    @Override
    public void setValue(final SequentialId id, final float value) {
        ValueGroup valueGroup = neuronValues.computeIfAbsent(id, k -> new ValueGroup());

        valueGroup.clear();
        valueGroup.replace(id, value);
    }

    @Override
    public void addToValue(final SequentialId id, final float value, final SequentialId sourceId) {
        ValueGroup values = neuronValues.computeIfAbsent(id, k -> new ValueGroup());

        values.replace(sourceId, value);

        if (neuronMemory != null) {
            neuronMemory.setValue(id, value, sourceId);
        }
    }

    @Override
    public void clear() {
        neuronValues.clear();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ValueGroup {
        private final Map<SequentialId, Float> values = new HashMap<>();
        private float total = 0f;

        private void replace(final SequentialId id, final float value) {
            Float oldValue = values.replace(id, value);

            total += value;

            if (oldValue != null) {
                total -= oldValue;
            }
        }

        private void clear() {
            values.clear();
            total = 0f;
        }
    }
}
