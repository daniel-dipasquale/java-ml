package com.dipasquale.common.random.float2;

import com.dipasquale.common.ArgumentValidatorSupport;

import java.io.Serial;
import java.io.Serializable;

public final class DeterministicRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -5482523579406345116L;
    private static final double MAXIMUM_SAFE_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private long index;
    private final double size;
    private final long maximum;

    public DeterministicRandomSupport(final long size) {
        ArgumentValidatorSupport.ensureGreaterThan(size, 1L, "size");
        this.index = 0L;
        this.size = (double) (size - 1L);
        this.maximum = size;
    }

    @Override
    public double next() {
        double value = (double) index / size;

        index = (index + 1L) % maximum;

        return value * MAXIMUM_SAFE_VALUE_LESS_THAN_ONE;
    }
}
