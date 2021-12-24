package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class NeuronMemory implements Serializable {
    @Serial
    private static final long serialVersionUID = 9141691343444193499L;
    private final Genome genome;
    private final Map<String, NeuronState> states = new HashMap<>();

    boolean isOwnedBy(final Genome candidate) {
        return genome == candidate;
    }

    private static String getId(final String dimension, final Id nodeId) {
        return String.format("%s:%s", dimension, nodeId);
    }

    Float getValue(final String dimension, final Id nodeId) {
        String id = getId(dimension, nodeId);
        NeuronState state = states.get(id);

        if (state == null) {
            return null;
        }

        return state.getValue();
    }

    void setValue(final String dimension, final Id nodeId, final float value, final Id inputNodeId) {
        String id = getId(dimension, nodeId);
        NeuronState state = states.computeIfAbsent(id, k -> new NeuronState());

        state.put(inputNodeId, value);
    }
}
