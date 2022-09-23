package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NeatNeuronMemory implements NeuronMemory, Serializable {
    @Serial
    private static final long serialVersionUID = 9141691343444193499L;
    private final Genome genome;
    private final Map<String, Float> values = new HashMap<>();

    boolean isOwnedBy(final Genome candidate) {
        return genome == candidate;
    }

    private static String getId(final String dimension, final Id neuronId) {
        if (neuronId == null) {
            return dimension;
        }

        return String.format("%s:%s", dimension, neuronId);
    }

    Float getValue(final String dimension, final Id nodeId) {
        String id = getId(dimension, nodeId);

        return values.get(id);
    }

    float getValueOrDefault(final String dimension, final Id neuronId) {
        Float value = getValue(dimension, neuronId);

        if (value == null) {
            return 0f;
        }

        return value;
    }

    void setValue(final String dimension, final Id neuronId, final float value) {
        String id = getId(dimension, neuronId);

        values.put(id, value);
    }
}
