package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.LongCounter;

import java.io.Serial;
import java.io.Serializable;

public final class LongSequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private final LongCounter counter;
    private final long resetValue;

    public LongSequentialIdFactory(final LongCounter counter) {
        this.counter = counter;
        this.resetValue = counter.current();
    }

    @Override
    public SequentialId create() {
        long value = counter.increment();

        if (value == Long.MIN_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        return new LongSequentialId(value);
    }

    @Override
    public void reset() {
        counter.current(resetValue);
    }
}
