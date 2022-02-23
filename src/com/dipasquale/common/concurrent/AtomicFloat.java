package com.dipasquale.common.concurrent;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicFloat extends Number {
    @Serial
    private static final long serialVersionUID = -8628202897803025853L;
    private final AtomicInteger bits;

    private static int translateTo(final float value) {
        return Float.floatToRawIntBits(value);
    }

    public AtomicFloat(final float value) {
        this.bits = new AtomicInteger(translateTo(value));
    }

    private static float translateFrom(final int bits) {
        return Float.intBitsToFloat(bits);
    }

    @Override
    public int intValue() {
        return bits.intValue();
    }

    @Override
    public long longValue() {
        return bits.longValue();
    }

    @Override
    public float floatValue() {
        return translateFrom(bits.intValue());
    }

    @Override
    public double doubleValue() {
        return Double.longBitsToDouble(bits.longValue());
    }

    public float get() {
        return floatValue();
    }

    public void set(final float value) {
        bits.set(translateTo(value));
    }

    public float addAndGet(final float delta) {
        int value = bits.accumulateAndGet(-1, (oldBits, __) -> {
            float oldValue = translateFrom(oldBits);

            return translateTo(oldValue + delta);
        });

        return translateFrom(value);
    }
}
