package com.dipasquale.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RecyclableVersion {
    private int value = 0;
    private final int offset;
    private final int max;

    public int next() {
        int next = (offset + value + 1) % max;

        value = next - offset;

        return next;
    }

    public int current() {
        return value + offset;
    }
}
