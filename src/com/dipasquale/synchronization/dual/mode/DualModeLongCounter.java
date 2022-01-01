package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.LongCounter;
import com.dipasquale.common.PlainLongCounter;
import com.dipasquale.common.concurrent.AtomicLongCounter;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeLongCounter implements LongCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6310708806974536759L;
    private final ConcurrencyLevelState concurrencyLevelState;
    @EqualsAndHashCode.Include
    private LongCounter counter;

    private DualModeLongCounter(final ConcurrencyLevelState concurrencyLevelState, final long value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.counter = create(concurrencyLevelState, value);
    }

    public DualModeLongCounter(final int concurrencyLevel, final long value) {
        this(new ConcurrencyLevelState(concurrencyLevel), value);
    }

    public DualModeLongCounter(final int concurrencyLevel) {
        this(concurrencyLevel, -1L);
    }

    private static LongCounter create(final ConcurrencyLevelState concurrencyLevelState, final long value) {
        if (concurrencyLevelState.getCurrent() > 0) {
            return new AtomicLongCounter(value);
        }

        return new PlainLongCounter(value);
    }

    @Override
    public long increment(final long delta) {
        return counter.increment(delta);
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
