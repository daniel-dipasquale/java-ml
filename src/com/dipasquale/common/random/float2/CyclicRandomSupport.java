package com.dipasquale.common.random.float2;

public final class CyclicRandomSupport implements RandomSupport {
    private static final double MAX_VALUE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private long index;
    private final double size;
    private final long max;

    public CyclicRandomSupport(final long size) {
        this.index = 0L;
        this.size = (double) size - 1L;
        this.max = size;
    }

    @Override
    public double next() {
        double value = (double) index / size;

        index = (index + 1L) % max;

        if (Double.compare(value, 1D) < 0) {
            return value;
        }

        return MAX_VALUE;
    }
}
