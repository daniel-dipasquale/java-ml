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
    private final ConcurrencyLevelState concurrencyLevelState;
    private final int max;
    private final int offset;
    @EqualsAndHashCode.Include
    private IntegerValue integerValue;

    private DualModeCyclicIntegerValue(final ConcurrencyLevelState concurrencyLevelState, final int max, final int offset, final int value) {
        this.concurrencyLevelState = concurrencyLevelState;
        this.max = max;
        this.offset = offset;
        this.integerValue = create(concurrencyLevelState, max, offset, value);
    }

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int max, final int offset, final int value) {
        this(new ConcurrencyLevelState(concurrencyLevel), max, offset, value);
    }

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int max, final int offset) {
        this(concurrencyLevel, max, offset, 0);
    }

    public DualModeCyclicIntegerValue(final int concurrencyLevel, final int max) {
        this(concurrencyLevel, max, -1);
    }

    private static IntegerValue create(final ConcurrencyLevelState concurrencyLevelState, final int max, final int offset, final int value) {
        if (concurrencyLevelState.getCurrent() > 0) {
            return new AtomicCyclicIntegerValue(max, offset, value);
        }

        return new CyclicIntegerValue(max, offset, value);
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
        integerValue = create(concurrencyLevelState, max, offset, integerValue.current() - offset);
    }

    @Override
    public String toString() {
        return integerValue.toString();
    }
}
