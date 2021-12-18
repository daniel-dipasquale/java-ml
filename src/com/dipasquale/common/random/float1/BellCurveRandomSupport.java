package com.dipasquale.common.random.float1;

import java.io.Serial;
import java.io.Serializable;

public final class BellCurveRandomSupport implements RandomSupport, Serializable { // based on : https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -2580047376540693357L;
    private final RandomSupport randomSupport;
    private final int iterations;
    private final float ratio;

    public BellCurveRandomSupport(final RandomSupport randomSupport, final int iterations) {
        this.randomSupport = randomSupport;
        this.iterations = iterations;
        this.ratio = 1f / (float) iterations;
    }

    @Override
    public float next() {
        float value = 0f;

        for (int i = 0; i < iterations; i++) {
            value += randomSupport.next() * ratio;
        }

        return value;
    }
}
