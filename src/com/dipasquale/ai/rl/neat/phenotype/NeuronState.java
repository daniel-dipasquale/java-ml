package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.Id;
import lombok.AccessLevel;
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
    private float currentValue = 0f;

    public float getValue() {
        return currentValue;
    }

    public void put(final Id id, final float value) {
        Float oldValue = values.put(id, value);

        if (oldValue != null) {
            float delta = value - oldValue;

            currentValue += delta;
        } else {
            currentValue += value;
        }
    }

    public void clear() {
        values.clear();
        currentValue = 0f;
    }
}
