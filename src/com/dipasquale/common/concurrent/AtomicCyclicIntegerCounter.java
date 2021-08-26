package com.dipasquale.common.concurrent;

import com.dipasquale.common.IntegerCounter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicCyclicIntegerCounter implements IntegerCounter, Serializable {
    @Serial
    private static final long serialVersionUID = -1148140015461196927L;
    private final int max;
    private final int offset;
    private final AtomicReference<Counter> counter;

    public AtomicCyclicIntegerCounter(final int max, final int offset, final int value) {
        this.max = max;
        this.offset = offset;
        this.counter = new AtomicReference<>(createCounter(offset, value, 0, max));
    }

    public AtomicCyclicIntegerCounter(final int max, final int offset) {
        this(max, offset, 0);
    }

    public AtomicCyclicIntegerCounter(final int max) {
        this(max, -1);
    }

    private static int calculateModulus(final int offset, final int counter, final int delta, final int max) {
        int remainder = (offset + counter + delta) % max;

        return (remainder + max) % max;
    }

    private static Counter createCounter(final int offset, final int counter, final int delta, final int max) {
        int total = calculateModulus(offset, counter, delta, max);
        int value = calculateModulus(0, counter, delta, max);

        return new Counter(total, value);
    }

    @Override
    public int increment(final int delta) {
        return counter.accumulateAndGet(null, (oc, nc) -> createCounter(offset, oc.value, delta, max)).total;
    }

    @Override
    public int current() {
        return counter.get().total;
    }

    @Override
    public int current(final int value) {
        Counter counterFixed = createCounter(offset, value, 0, max);

        counter.set(counterFixed);

        return counterFixed.total;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(counter.get().total, calculateModulus(offset, other, 0, max));
    }

    @Override
    public int hashCode() {
        return counter.get().total;
    }

    private boolean equals(final AtomicCyclicIntegerCounter other) {
        return counter.get().total == other.counter.get().total;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicCyclicIntegerCounter) {
            return equals((AtomicCyclicIntegerCounter) other);
        }

        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(counter.get().total);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Counter implements Serializable {
        @Serial
        private static final long serialVersionUID = -1597823702953196892L;
        private final int total;
        private final int value;
    }
}
