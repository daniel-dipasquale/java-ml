package com.dipasquale.ai.common.sequence;

import java.io.Serial;
import java.io.Serializable;

public final class LongSequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private long current;

    public LongSequentialIdFactory() {
        this.current = 0L;
    }

    public LongSequentialIdFactory(final LongSequentialIdFactory other) {
        this.current = other.current;
    }

    @Override
    public SequentialId create() {
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        long value = ++current;

        return new LongSequentialId(value);
    }

    @Override
    public void reset() {
        current = 0L;
    }
}
