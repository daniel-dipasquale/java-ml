/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.bit;

final class Only64BitManipulatorSupport implements BitManipulatorSupport {
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

