package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SumFitnessDeterminer implements FitnessDeterminer, Serializable {
    @Serial
    private static final long serialVersionUID = 122823839162210555L;
    private float value = 0f;

    @Override
    public float get() {
        return value;
    }

    @Override
    public void add(final float fitness) {
        value += fitness;
    }

    @Override
    public void clear() {
        value = 0f;
    }
}
