package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.LongValue;

import java.io.Serial;
import java.io.Serializable;

public final class LongSequentialIdFactory implements SequentialIdFactory<LongSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private final LongValue currentValue;
    private final long initialValue;

    public LongSequentialIdFactory(final LongValue value) {
        this.currentValue = value;
        this.initialValue = value.current();
    }

    @Override
    public LongSequentialId create() {
        long value = currentValue.increment();

        if (value == Long.MIN_VALUE) {
            throw new SequenceEndedException();
        }

        return new LongSequentialId(value);
    }

    @Override
    public void reset() {
        currentValue.current(initialValue);
    }
}
