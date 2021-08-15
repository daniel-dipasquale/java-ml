package com.dipasquale.common.random.float1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConstantRandomSupport implements RandomSupport {
    private static final float MAX_VALUE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private final int size;
    private int index = 0;

    @Override
    public float next() {
        float value = (float) index / (float) size;

        index = (index + 1) % (size + 1);

        if (Float.compare(value, 1f) < 0) {
            return value;
        }

        return MAX_VALUE;
    }
}
