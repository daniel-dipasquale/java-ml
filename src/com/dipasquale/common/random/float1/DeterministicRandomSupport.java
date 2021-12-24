package com.dipasquale.common.random.float1;

import com.dipasquale.common.ArgumentValidatorSupport;

import java.io.Serial;
import java.io.Serializable;

public final class DeterministicRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 199669929765894032L;
    private static final float MAX_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private int index;
    private final float size;
    private final int max;

    public DeterministicRandomSupport(final int size) {
        ArgumentValidatorSupport.ensureGreaterThan(size, 1, "size");
        this.index = 0;
        this.size = (float) (size - 1);
        this.max = size;
    }

    @Override
    public float next() {
        float value = (float) index / size;

        index = (index + 1) % max;

        return value * MAX_VALUE_LESS_THAN_ONE;
    }
}
