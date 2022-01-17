package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.PlainIntegerValue;
import com.dipasquale.common.concurrent.AtomicIntegerValue;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeIntegerValue implements IntegerValue, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -7472742036609010053L;
    @EqualsAndHashCode.Include
    private IntegerValue integerValue;

    public DualModeIntegerValue(final int concurrencyLevel, final int value) {
        this.integerValue = create(concurrencyLevel, value);
    }

    public DualModeIntegerValue(final int concurrencyLevel) {
        this(concurrencyLevel, -1);
    }

    private static IntegerValue create(final int concurrencyLevel, final int value) {
        if (concurrencyLevel > 0) {
            return new AtomicIntegerValue(value);
        }

        return new PlainIntegerValue(value);
    }

    @Override
    public int current() {
        return integerValue.current();
    }

    @Override
    public int current(final int value) {
        return integerValue.current(value);
    }

    @Override
    public int increment(final int delta) {
        return integerValue.increment(delta);
    }

    @Override
    public int compareTo(final Integer other) {
        return integerValue.compareTo(other);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        integerValue = create(concurrencyLevel, integerValue.current());
    }
}
