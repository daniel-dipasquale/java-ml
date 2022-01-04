package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.LongValue;
import com.dipasquale.common.PlainLongValue;
import com.dipasquale.common.concurrent.AtomicLongValue;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeLongValue implements LongValue, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6310708806974536759L;
    private final ConcurrencyLevelState concurrencyLevelState;
    @EqualsAndHashCode.Include
    private LongValue counter;

    private DualModeLongValue(final ConcurrencyLevelState concurrencyLevelState, final long value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.counter = create(concurrencyLevelState, value);
    }

    public DualModeLongValue(final int concurrencyLevel, final long value) {
        this(new ConcurrencyLevelState(concurrencyLevel), value);
    }

    public DualModeLongValue(final int concurrencyLevel) {
        this(concurrencyLevel, -1L);
    }

    private static LongValue create(final ConcurrencyLevelState concurrencyLevelState, final long value) {
        if (concurrencyLevelState.getCurrent() > 0) {
            return new AtomicLongValue(value);
        }

        return new PlainLongValue(value);
    }

    @Override
    public long current() {
        return counter.current();
    }

    @Override
    public long current(final long value) {
        return counter.current(value);
    }

    @Override
    public long increment(final long delta) {
        return counter.increment(delta);
    }

    @Override
    public int compareTo(final Long other) {
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
