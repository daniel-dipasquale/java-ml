package com.dipasquale.common.concurrent;

import com.dipasquale.common.IntegerValue;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerValue implements IntegerValue, Serializable {
    @Serial
    private static final long serialVersionUID = 1905238796983652368L;
    private final AtomicInteger raw;

    public AtomicIntegerValue(final int value) {
        this.raw = new AtomicInteger(value);
    }

    public AtomicIntegerValue() {
        this(-1);
    }

    @Override
    public int current() {
        return raw.get();
    }

    @Override
    public int current(final int value) {
        raw.set(value);

        return value;
    }

    @Override
    public int increment(final int delta) {
        return raw.addAndGet(delta);
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(raw.get(), other);
    }

    @Override
    public int hashCode() {
        return raw.get();
    }

    private boolean equals(final AtomicIntegerValue other) {
        return raw.get() == other.raw.get();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicIntegerValue otherFixed) {
            return equals(otherFixed);
        }

        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(raw.get());
    }
}
