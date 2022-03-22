package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AverageFitnessController implements FitnessController, Serializable {
    @Serial
    private static final long serialVersionUID = -866540688453564894L;
    private float sum = 0f;
    private int count = 0;

    @Override
    public float get() {
        if (count == 0) {
            return 0f;
        }

        return sum / (float) count;
    }

    @Override
    public void add(final float fitness) {
        sum += fitness;
        count++;
    }

    @Override
    public void clear() {
        sum = 0f;
        count = 0;
    }
}
