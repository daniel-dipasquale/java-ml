package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.FloatValue;
import com.dipasquale.common.PlainFloatValue;
import com.dipasquale.common.concurrent.AtomicFloatValue;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeFloatValue implements FloatValue, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2200200182292131268L;
    @EqualsAndHashCode.Include
    private FloatValue floatValue;

    public DualModeFloatValue(final int concurrencyLevel, final float value) {
        this.floatValue = create(concurrencyLevel, value);
    }

    public DualModeFloatValue(final int concurrencyLevel) {
        this(concurrencyLevel, -1);
    }

    private static FloatValue create(final int concurrencyLevel, final float value) {
        if (concurrencyLevel > 0) {
            return new AtomicFloatValue(value);
        }

        return new PlainFloatValue(value);
    }

    @Override
    public float current() {
        return floatValue.current();
    }

    @Override
    public float current(final float value) {
        return floatValue.current(value);
    }

    @Override
    public float increment(final float delta) {
        return floatValue.increment(delta);
    }

    @Override
    public int compareTo(final Float other) {
        return floatValue.compareTo(other);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        floatValue = create(concurrencyLevel, floatValue.current());
    }
}
