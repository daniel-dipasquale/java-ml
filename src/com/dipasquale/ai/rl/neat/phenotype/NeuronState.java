package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
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

    public void replace(final Id id, final float newValue) {
        Float oldValue = values.replace(id, newValue);

        value += newValue; // TODO: add an assert to ensure the SUM(values.values()) == value

        if (oldValue != null) {
            value -= oldValue;
        }
    }

    public void clear() {
        values.clear();
        value = 0f;
    }
}
