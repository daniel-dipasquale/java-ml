package com.dipasquale.common.concurrent;

import com.dipasquale.common.FloatValue;

import java.io.Serial;
import java.io.Serializable;

public final class AtomicFloatValue implements FloatValue, Serializable {
    @Serial
    private static final long serialVersionUID = 8170039813453142999L;
    private final AtomicFloat raw;

    public AtomicFloatValue(final float value) {
        this.raw = new AtomicFloat(value);
    }

    public AtomicFloatValue() {
        this(0f);
    }

    @Override
    public float current() {
        return raw.get();
    }

    @Override
    public float current(final float value) {
        raw.set(value);

        return value;
    }

    @Override
    public float increment(final float delta) {
        return raw.addAndGet(delta);
    }

    @Override
    public int compareTo(final Float other) {
        return Float.compare(raw.get(), other);
    }

    @Override
    public int hashCode() {
        return raw.intValue();
    }

    private boolean equals(final AtomicFloatValue other) {
        return Float.compare(raw.get(), other.raw.get()) == 0;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicFloatValue otherFixed) {
            return equals(otherFixed);
        }

        return false;
    }

    @Override
    public String toString() {
        return Float.toString(raw.get());
    }
}
