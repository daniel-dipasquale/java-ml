package com.dipasquale.ai.common.fitness;

import java.io.Serial;
import java.io.Serializable;

final class LastValueFitnessController implements FitnessController, Serializable {
    @Serial
    private static final long serialVersionUID = 1119352029838131618L;
    private float value = 0f;

    @Override
    public float get() {
        return value;
    }

    @Override
    public void add(final float fitness) {
        value = fitness;
    }

    @Override
    public void clear() {
        value = 0f;
    }
}
