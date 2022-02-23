package com.dipasquale.common;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class CyclicIntegerValue implements IntegerValue, Serializable {
    @Serial
    private static final long serialVersionUID = 5427930261018368922L;
    private final int maximum;
    private final int offset;
    @EqualsAndHashCode.Include
    private int total;
    private int current;

    private static int calculateModulus(final int offset, final int counter, final int delta, final int maximum) {
        int remainder = (offset + counter + delta) % maximum;

        return (remainder + maximum) % maximum;
    }

    public CyclicIntegerValue(final int maximum, final int offset, final int value) {
        this.maximum = maximum;
        this.offset = offset;
        this.total = calculateModulus(offset, value, 0, maximum);
        this.current = calculateModulus(0, value, 0, maximum);
    }

    public CyclicIntegerValue(final int maximum, final int offset) {
        this(maximum, offset, 0);
    }

    public CyclicIntegerValue(final int maximum) {
        this(maximum, -1);
    }

    @Override
    public int current() {
        return total;
    }

    @Override
    public int current(final int value) {
        total = calculateModulus(offset, value, 0, maximum);
        current = calculateModulus(0, value, 0, maximum);

        return total;
    }

    @Override
    public int increment(final int delta) {
        total = calculateModulus(offset, current, delta, maximum);
        current = calculateModulus(0, current, delta, maximum);

        return total;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(total, calculateModulus(offset, other, 0, maximum));
    }

    @Override
    public String toString() {
        return Integer.toString(total);
    }
}
