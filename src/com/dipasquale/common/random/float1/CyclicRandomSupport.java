package com.dipasquale.common.random.float1;

public final class CyclicRandomSupport implements RandomSupport {
    private static final float MAX_VALUE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private int index;
    private final float size;
    private final int max;

    public CyclicRandomSupport(final int size) {
        this.index = 0;
        this.size = (float) size;
        this.max = size + 1;
    }

    @Override
    public float next() {
        float value = (float) index / size;

        index = (index + 1) % max;

        if (Float.compare(value, 1f) < 0) {
            return value;
        }

        return MAX_VALUE;
    }
}
