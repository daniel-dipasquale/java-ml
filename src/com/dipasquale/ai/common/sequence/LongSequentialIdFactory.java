package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.LongValue;

import java.io.Serial;
import java.io.Serializable;

public final class LongSequentialIdFactory implements SequentialIdFactory<LongSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private final LongValue counter;
    private final long resetValue;

    public LongSequentialIdFactory(final LongValue counter) {
        this.counter = counter;
        this.resetValue = counter.current();
    }

    @Override
    public LongSequentialId create() {
        long value = counter.increment();

        if (value == Long.MIN_VALUE) {
            throw new SequenceEndedException();
        }

        return new LongSequentialId(value);
    }

    @Override
    public void reset() {
        counter.current(resetValue);
    }
}
