package com.dipasquale.common.bit.int1;

final class Only32BitManipulatorSupport implements BitManipulatorSupport {
    private static final int SIZE = 1;

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isOutOfBounds(final int value) {
        return false;
    }

    @Override
    public int extract(final int raw, final int offset) {
        return raw;
    }

    @Override
    public int merge(final int raw, final int offset, final int value) {
        return value;
    }
}

