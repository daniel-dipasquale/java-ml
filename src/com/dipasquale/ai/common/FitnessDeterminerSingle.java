package com.dipasquale.ai.common;

final class FitnessDeterminerSingle implements FitnessDeterminer {
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
