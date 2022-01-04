package com.dipasquale.common;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class CyclicIntegerValue implements IntegerValue, Serializable {
    @Serial
    private static final long serialVersionUID = 5427930261018368922L;
    private final int max;
    private final int offset;
    @EqualsAndHashCode.Include
    private int total;
    private int current;

    public CyclicIntegerValue(final int max, final int offset, final int value) {
        this.max = max;
        this.offset = offset;
        this.total = calculateModulus(offset, value, 0, max);
        this.current = calculateModulus(0, value, 0, max);
    }

    public CyclicIntegerValue(final int max, final int offset) {
        this(max, offset, 0);
    }

    public CyclicIntegerValue(final int max) {
        this(max, -1);
    }

    private static int calculateModulus(final int offset, final int counter, final int delta, final int max) {
        int remainder = (offset + counter + delta) % max;

        return (remainder + max) % max;
    }

    @Override
    public int current() {
        return total;
    }

    @Override
    public int current(final int value) {
        total = calculateModulus(offset, value, 0, max);
        current = calculateModulus(0, value, 0, max);

        return total;
    }

    @Override
    public int increment(final int delta) {
        total = calculateModulus(offset, current, delta, max);
        current = calculateModulus(0, current, delta, max);

        return total;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(total, calculateModulus(offset, other, 0, max));
    }

    @Override
    public String toString() {
        return Integer.toString(total);
    }
}
