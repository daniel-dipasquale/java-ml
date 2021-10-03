package com.dipasquale.synchronization.dual.mode;

import com.dipasquale.common.CyclicIntegerCounter;
import com.dipasquale.common.IntegerCounter;
import com.dipasquale.common.concurrent.AtomicCyclicIntegerCounter;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DualModeCyclicIntegerCounter implements IntegerCounter, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8709366941955699964L;
    private final ConcurrencyLevelState concurrencyLevelState;
    private final int max;
    private final int offset;
    @EqualsAndHashCode.Include
    private IntegerCounter counter;

    private DualModeCyclicIntegerCounter(final ConcurrencyLevelState concurrencyLevelState, final int max, final int offset, final int value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.max = max;
        this.offset = offset;
        this.counter = create(concurrencyLevelState, max, offset, value);
    }

    public DualModeCyclicIntegerCounter(final int concurrencyLevel, final int max, final int offset, final int value) {
        this(new ConcurrencyLevelState(concurrencyLevel), max, offset, value);
    }

    public DualModeCyclicIntegerCounter(final int concurrencyLevel, final int max, final int offset) {
        this(concurrencyLevel, max, offset, 0);
    }

    public DualModeCyclicIntegerCounter(final int concurrencyLevel, final int max) {
        this(concurrencyLevel, max, -1);
    }

    private static IntegerCounter create(final ConcurrencyLevelState concurrencyLevelState, final int max, final int offset, final int value) {
        if (concurrencyLevelState.getCurrent() > 0) {
            return new AtomicCyclicIntegerCounter(max, offset, value);
        }

        return new CyclicIntegerCounter(max, offset, value);
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
        counter = create(concurrencyLevelState, max, offset, counter.current());
    }
}
