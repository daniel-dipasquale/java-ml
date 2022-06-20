package com.dipasquale.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class StandardFloatValue implements FloatValue, Serializable {
    @Serial
    private static final long serialVersionUID = 4407313574791902479L;
    private float raw = 0f;

    @Override
    public float current() {
        return raw;
    }

    @Override
    public float current(final float value) {
        return raw = value;
    }

    @Override
    public float increment(final float delta) {
        return raw += delta;
    }

    @Override
    public int compareTo(final Float other) {
        return Float.compare(raw, other);
    }

    @Override
    public String toString() {
        return Float.toString(raw);
    }
}
