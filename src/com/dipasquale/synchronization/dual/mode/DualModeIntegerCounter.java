package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.IntegerCounter;
import com.dipasquale.common.PlainIntegerCounter;
import com.dipasquale.common.concurrent.AtomicIntegerCounter;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeIntegerCounter implements IntegerCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -7472742036609010053L;
    private final ConcurrencyLevelState concurrencyLevelState;
    @EqualsAndHashCode.Include
    private IntegerCounter counter;

    private DualModeIntegerCounter(final ConcurrencyLevelState concurrencyLevelState, final int value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.counter = create(concurrencyLevelState, value);
    }

    public DualModeIntegerCounter(final int concurrencyLevel, final int value) {
        this(new ConcurrencyLevelState(concurrencyLevel), value);
    }

    public DualModeIntegerCounter(final int concurrencyLevel) {
        this(concurrencyLevel, -1);
    }

    private static IntegerCounter create(final ConcurrencyLevelState concurrencyLevelState, final int value) {
        if (concurrencyLevelState.getCurrent() > 0) {
            return new AtomicIntegerCounter(value);
        }

        return new PlainIntegerCounter(value);
    }

    @Override
    public int increment(final int delta) {
        return counter.increment(delta);
    }

    @Override
    public int current() {
        return counter.current();
    }

    @Override
    public int current(final int value) {
        return counter.current(value);
    }

    @Override
    public int compareTo(final Integer other) {
        return counter.compareTo(other);
    }

    @Override
    public int concurrencyLevel() {
        return concurrencyLevelState.getCurrent();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        concurrencyLevelState.setCurrent(concurrencyLevel);
        counter = create(concurrencyLevelState, counter.current());
    }
}
