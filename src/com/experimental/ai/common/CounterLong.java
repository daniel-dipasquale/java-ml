package com.experimental.ai.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class CounterLong implements Counter<Long> {
    private long current;
    private long delta;

    @Override
    public Long next() {
        return current += delta;
    }

    @Override
    public Long current() {
        return current;
    }

    public static CounterLong createForward() {
        return new CounterLong(0L, 1L);
    }

    public static CounterLong createBackward() {
        return new CounterLong(Long.MAX_VALUE, -1L);
    }
}
