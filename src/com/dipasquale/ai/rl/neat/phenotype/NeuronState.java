package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronState implements Serializable {
    @Serial
    private static final long serialVersionUID = 5859080064835741130L;
    private final Map<Id, Float> values = new HashMap<>();
    @Getter
    private float value = 0f;

    public void put(final Id id, final float newValue) {
        Float oldValue = values.put(id, newValue);

        if (oldValue != null) {
            value += (newValue - oldValue);
        } else {
            value += newValue;
        }
    }

    public void clear() {
        values.clear();
        value = 0f;
    }
}
