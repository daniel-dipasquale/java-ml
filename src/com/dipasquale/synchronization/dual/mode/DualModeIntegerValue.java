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
    private final ConcurrencyLevelState concurrencyLevelState;
    @EqualsAndHashCode.Include
    private IntegerValue integerValue;

    private DualModeIntegerValue(final ConcurrencyLevelState concurrencyLevelState, final int value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.integerValue = create(concurrencyLevelState, value);
    }

    public DualModeIntegerValue(final int concurrencyLevel, final int value) {
        this(new ConcurrencyLevelState(concurrencyLevel), value);
    }

    public DualModeIntegerValue(final int concurrencyLevel) {
        this(concurrencyLevel, -1);
    }

    private static IntegerValue create(final ConcurrencyLevelState concurrencyLevelState, final int value) {
        if (concurrencyLevelState.getCurrent() > 0) {
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
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
        integerValue = create(concurrencyLevelState, integerValue.current());
    }
}
