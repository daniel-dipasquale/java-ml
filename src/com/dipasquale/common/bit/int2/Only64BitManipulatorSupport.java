package com.dipasquale.common.bit.int2;

final class Only64BitManipulatorSupport implements BitManipulatorSupport {
    private static final long SIZE = 1L;

    @Override
    public long size() {
        return SIZE;
    }

    @Override
    public boolean isWithinBounds(final long value) {
        return true;
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

