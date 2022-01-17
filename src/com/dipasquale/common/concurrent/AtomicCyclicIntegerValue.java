package com.dipasquale.common.concurrent;

import com.dipasquale.common.IntegerValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicCyclicIntegerValue implements IntegerValue, Serializable {
    @Serial
    private static final long serialVersionUID = -1148140015461196927L;
    private final int maximum;
    private final int offset;
    private final AtomicReference<Pair> pair;

    public AtomicCyclicIntegerValue(final int maximum, final int offset, final int value) {
        this.maximum = maximum;
        this.offset = offset;
        this.pair = new AtomicReference<>(createPair(offset, value, 0, maximum));
    }

    public AtomicCyclicIntegerValue(final int maximum, final int offset) {
        this(maximum, offset, 0);
    }

    public AtomicCyclicIntegerValue(final int maximum) {
        this(maximum, -1);
    }

    private static int calculateModulus(final int offset, final int counter, final int delta, final int maximum) {
        int remainder = (offset + counter + delta) % maximum;

        return (remainder + maximum) % maximum;
    }

    private static Pair createPair(final int offset, final int counter, final int delta, final int maximum) {
        int total = calculateModulus(offset, counter, delta, maximum);
        int value = calculateModulus(0, counter, delta, maximum);

        return new Pair(total, value);
    }

    @Override
    public int current() {
        return pair.get().total;
    }

    @Override
    public int current(final int value) {
        Pair pairFixed = createPair(offset, value, 0, maximum);

        pair.set(pairFixed);

        return pairFixed.total;
    }

    @Override
    public int increment(final int delta) {
        return pair.accumulateAndGet(null, (oldPair, newPair) -> createPair(offset, oldPair.value, delta, maximum)).total;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(pair.get().total, calculateModulus(offset, other, 0, maximum));
    }

    @Override
    public int hashCode() {
        return pair.get().total;
    }

    private boolean equals(final AtomicCyclicIntegerValue other) {
        return pair.get().total == other.pair.get().total;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicCyclicIntegerValue otherFixed) {
            return equals(otherFixed);
        }

        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(pair.get().total);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Pair implements Serializable {
        @Serial
        private static final long serialVersionUID = -1597823702953196892L;
        private final int total;
        private final int value;
    }
}
