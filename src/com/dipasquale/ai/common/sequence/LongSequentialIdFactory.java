package com.dipasquale.ai.common.sequence;

import com.dipasquale.common.LongCounter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LongSequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private final LongCounter counter;

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
        counter.current(0L);
    }
}
