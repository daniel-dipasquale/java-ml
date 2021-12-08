package com.dipasquale.common.concurrent;

import com.dipasquale.common.IntegerCounter;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerCounter implements IntegerCounter, Serializable {
    @Serial
    private static final long serialVersionUID = 1905238796983652368L;
    private final AtomicInteger counter;

    public AtomicIntegerCounter(final int value) {
        this.counter = new AtomicInteger(value);
    }

    public AtomicIntegerCounter() {
        this(-1);
    }

    @Override
    public int increment(final int delta) {
        return counter.addAndGet(delta);
    }

    @Override
    public int current() {
        return counter.get();
    }

    @Override
    public int current(final int value) {
        counter.set(value);

        return value;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(counter.get(), other);
    }

    @Override
    public int hashCode() {
        return counter.get();
    }

    private boolean equals(final AtomicIntegerCounter other) {
        return counter.get() == other.counter.get();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicIntegerCounter otherFixed) {
            return equals(otherFixed);
        }

        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(counter.get());
    }
}
