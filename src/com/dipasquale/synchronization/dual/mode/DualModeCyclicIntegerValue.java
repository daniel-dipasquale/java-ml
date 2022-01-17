package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.CyclicIntegerValue;
import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.concurrent.AtomicCyclicIntegerValue;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeCyclicIntegerValue implements IntegerValue, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8709366941955699964L;
    private final int maximum;
    private final int offset;
    @EqualsAndHashCode.Include
    private IntegerValue integerValue;

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int maximum, final int offset, final int value) {
        this.maximum = maximum;
        this.offset = offset;
        this.integerValue = create(concurrencyLevel, maximum, offset, value);
    }

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int maximum, final int offset) {
        this(concurrencyLevel, maximum, offset, 0);
    }

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int maximum) {
        this(concurrencyLevel, maximum, -1);
    }

    private static IntegerValue create(final int concurrencyLevel, final int maximum, final int offset, final int value) {
        if (concurrencyLevel > 0) {
            return new AtomicCyclicIntegerValue(maximum, offset, value);
        }

        return new CyclicIntegerValue(maximum, offset, value);
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
        integerValue = create(concurrencyLevel, maximum, offset, integerValue.current() - offset);
    }

    @Override
    public String toString() {
        return integerValue.toString();
    }
}
