package com.dipasquale.ai.common;

import java.io.Serial;

public final class SequentialIdFactoryDefault implements SequentialIdFactory {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private long current;

    public SequentialIdFactoryDefault() {
        this.current = 0L;
    }

    public SequentialIdFactoryDefault(final SequentialIdFactoryDefault other) {
        this.current = other.current;
    }

    @Override
    public SequentialId create() {
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        long value = ++current;

        return new SequentialIdDefault(value);
    }

    @Override
    public void reset() {
        current = 0L;
    }
}
