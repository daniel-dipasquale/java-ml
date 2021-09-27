package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MaximumFitnessDeterminer implements FitnessDeterminer, Serializable {
    @Serial
    private static final long serialVersionUID = -6792059173769832477L;
    private boolean initialized = false;
    private float value = 0f;

    @Override
    public float get() {
        return value;
    }

    @Override
    public void add(final float fitness) {
        if (!initialized) {
            initialized = true;
            value = fitness;
        } else {
            value = Math.max(value, fitness);
        }
    }

    @Override
    public void clear() {
        initialized = false;
        value = 0f;
    }
}
