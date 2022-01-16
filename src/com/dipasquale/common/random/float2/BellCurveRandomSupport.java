package com.dipasquale.common.random.float2;

import java.io.Serial;
import java.io.Serializable;

public final class BellCurveRandomSupport implements RandomSupport, Serializable { // based on: https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -1870741538456358942L;
    private final RandomSupport randomSupport;
    private final int iterations;
    private final double rate;

    public BellCurveRandomSupport(final RandomSupport randomSupport, final int iterations) {
        this.randomSupport = randomSupport;
        this.iterations = iterations;
        this.rate = 1D / (double) iterations;
    }

    @Override
    public double next() {
        double value = 0D;

        for (int i = 0; i < iterations; i++) {
            value += randomSupport.next() * rate;
        }

        return value;
    }
}
