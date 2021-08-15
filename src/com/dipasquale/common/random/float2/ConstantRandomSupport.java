package com.dipasquale.common.random.float2;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConstantRandomSupport implements RandomSupport {
    private static final double MAX_VALUE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private final long size;
    private long index = 0L;

    @Override
    public double next() {
        double value = (double) index / (double) size;

        index = (index + 1L) % (size + 1L);

        if (Double.compare(value, 1D) < 0) {
            return value;
        }

        return MAX_VALUE;
    }
}
