package com.dipasquale.common.random;

import java.io.Serial;
import java.io.Serializable;

public final class BellCurveRandomSupport implements RandomSupport, Serializable { // based on : https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -2580047376540693357L;
    private final RandomSupport randomSupport;
    private final int iterations;
    private final float floatRate;
    private final double doubleRate;

    public BellCurveRandomSupport(final RandomSupport randomSupport, final int iterations) {
        this.randomSupport = randomSupport;
        this.iterations = iterations;
        this.floatRate = 1f / (float) iterations;
        this.doubleRate = 1D / (double) iterations;
    }

    @Override
    public float nextFloat() {
        float value = 0f;

        for (int i = 0; i < iterations; i++) {
            value += randomSupport.nextFloat() * floatRate;
        }

        return value;
    }

    @Override
    public double nextDouble() {
        double value = 0D;

        for (int i = 0; i < iterations; i++) {
            value += randomSupport.nextDouble() * doubleRate;
        }

        return value;
    }
}
