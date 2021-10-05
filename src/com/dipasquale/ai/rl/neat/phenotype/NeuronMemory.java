package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NeuronMemory {
    private final Genome genome;
    private final Map<SequentialId, ValueGroup> neuronValues = new HashMap<>();

    boolean isOwnedBy(final Genome candidate) {
        return genome == candidate;
    }

    Float getValue(final SequentialId id) {
        ValueGroup values = neuronValues.get(id);

        if (values == null) {
            return null;
        }

        return values.total;
    }

    void setValue(final SequentialId id, final float value, final SequentialId sourceId) {
        ValueGroup values = neuronValues.computeIfAbsent(id, k -> new ValueGroup());

        values.replace(sourceId, value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ValueGroup {
        private final Map<SequentialId, Float> values = new HashMap<>();
        private float total = 0f;

        private void replace(final SequentialId id, final float value) {
            Float oldValue = values.replace(id, value);

            total += value; // TODO: add an assert to ensure the SUM(values) == total

            if (oldValue != null) {
                total -= oldValue;
            }
        }
    }
}
