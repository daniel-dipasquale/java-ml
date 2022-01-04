package com.dipasquale.common.concurrent;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicDouble extends Number {
    @Serial
    private static final long serialVersionUID = -8430898427674005626L;
    private final AtomicLong bits;

    public AtomicDouble(final double value) {
        this.bits = new AtomicLong(translateTo(value));
    }

    private static long translateTo(final double value) {
        return Double.doubleToRawLongBits(value);
    }

    private static double translateFrom(final long value) {
        return Double.longBitsToDouble(value);
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
        return Float.intBitsToFloat(bits.intValue());
    }

    @Override
    public double doubleValue() {
        return translateFrom(bits.longValue());
    }

    public double get() {
        return doubleValue();
    }

    public void set(final double value) {
        bits.set(translateTo(value));
    }
}
