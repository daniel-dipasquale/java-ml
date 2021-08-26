package com.dipasquale.common;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class CyclicIntegerCounter implements IntegerCounter, Serializable {
    @Serial
    private static final long serialVersionUID = 5427930261018368922L;
    private final int max;
    private final int offset;
    @EqualsAndHashCode.Include
    private int counterTotal;
    private int counterValue;

    public CyclicIntegerCounter(final int max, final int offset, final int value) {
        this.max = max;
        this.offset = offset;
        this.counterTotal = calculateModulus(offset, value, 0, max);
        this.counterValue = calculateModulus(0, value, 0, max);
    }

    public CyclicIntegerCounter(final int max, final int offset) {
        this(max, offset, 0);
    }

    public CyclicIntegerCounter(final int max) {
        this(max, -1);
    }

    private static int calculateModulus(final int offset, final int counter, final int delta, final int max) {
        int remainder = (offset + counter + delta) % max;

        return (remainder + max) % max;
    }

    @Override
    public int increment(final int delta) {
        counterTotal = calculateModulus(offset, counterValue, delta, max);
        counterValue = calculateModulus(0, counterValue, delta, max);

        return counterTotal;
    }

    @Override
    public int current() {
        return counterTotal;
    }

    @Override
    public int current(final int value) {
        counterTotal = calculateModulus(offset, value, 0, max);
        counterValue = calculateModulus(0, value, 0, max);

        return counterTotal;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(counterTotal, calculateModulus(offset, other, 0, max));
    }

    @Override
    public String toString() {
        return Integer.toString(counterTotal);
    }
}
