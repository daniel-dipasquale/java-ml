package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BitManipulatorSupport64 implements BitManipulatorSupport {
    private static final long SIZE = 1L;

    @Override
    public long size() {
        return SIZE;
    }

    @Override
    public boolean isOutOfBounds(final long value) {
        return false;
    }

    @Override
    public long extract(final long raw, final long offset) {
        return raw;
    }

    @Override
    public long merge(final long raw, final long offset, final long value) {
        return value;
    }
}

