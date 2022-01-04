package com.dipasquale.common.concurrent;

import com.dipasquale.common.LongValue;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongValue implements LongValue, Serializable {
    @Serial
    private static final long serialVersionUID = -7703255803251431507L;
    private final AtomicLong raw;

    public AtomicLongValue(final long value) {
        this.raw = new AtomicLong(value);
    }

    public AtomicLongValue() {
        this(-1L);
    }

    @Override
    public long current() {
        return raw.get();
    }

    @Override
    public long current(final long value) {
        raw.set(value);

        return value;
    }

    @Override
    public long increment(final long delta) {
        return raw.addAndGet(delta);
    }

    @Override
    public int compareTo(final Long other) {
        return Long.compare(raw.get(), other);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(raw.get());
    }

    private boolean equals(final AtomicLongValue other) {
        return raw.get() == other.raw.get();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicLongValue otherFixed) {
            return equals(otherFixed);
        }

        return false;
    }

    @Override
    public String toString() {
        return Long.toString(raw.get());
    }
}
