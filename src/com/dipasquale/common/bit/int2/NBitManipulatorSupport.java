package com.dipasquale.common.bit.int2;

final class NBitManipulatorSupport implements BitManipulatorSupport {
    private final int bits;
    private final long mask;
    private final long size;

    NBitManipulatorSupport(final int bits) {
        this.bits = bits;
        this.mask = (1L << bits) - 1L;
        this.size = 64L / (long) bits;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isWithinBounds(final long value) {
        return value == (value & mask);
    }

    @Override
    public long extract(final long raw, final long offset) {
        int shifts = (int) offset * bits;

        return (raw >> shifts) & mask;
    }

    @Override
    public long merge(final long raw, final long offset, final long value) {
        int shifts = (int) offset * bits;
        long valueCapped = value & mask;
        long maskShifted = mask << shifts;
        long maskShiftedReversed = ~maskShifted;
        long rawCleared = raw & maskShiftedReversed;
        long valueCappedShifted = valueCapped << shifts;

        return rawCleared | valueCappedShifted;
    }
}
