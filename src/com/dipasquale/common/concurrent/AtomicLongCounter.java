package com.dipasquale.common.concurrent;

import com.dipasquale.common.LongCounter;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongCounter implements LongCounter, Serializable {
    @Serial
    private static final long serialVersionUID = -7703255803251431507L;
    private final AtomicLong counter;

    public AtomicLongCounter(final long value) {
        this.counter = new AtomicLong(value);
    }

    public AtomicLongCounter() {
        this(-1L);
    }

    @Override
    public long increment(final long delta) {
        return counter.addAndGet(delta);
    }

    @Override
    public long current() {
        return counter.get();
    }

    @Override
    public long current(final long value) {
        counter.set(value);

        return value;
    }

    @Override
    public int compareTo(final Long other) {
        return Long.compare(counter.get(), other);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(counter.get());
    }

    private boolean equals(final AtomicLongCounter other) {
        return counter.get() == other.counter.get();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicLongCounter) {
            return equals((AtomicLongCounter) other);
        }

        return false;
    }

    @Override
    public String toString() {
        return Long.toString(counter.get());
    }
}
