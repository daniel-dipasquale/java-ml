package com.dipasquale.common.bit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

final class DefaultBitManipulatorSupport implements BitManipulatorSupport {
    private final int bits;
    private final long mask;
    private final long size;
    @Getter(AccessLevel.PACKAGE)
    private final UnitTest unitTest;

    DefaultBitManipulatorSupport(final int bits) {
        this.bits = bits;
        this.mask = (1L << bits) - 1L;
        this.size = (long) BitManipulatorSupport.MAXIMUM_BITS / (long) bits;
        this.unitTest = new UnitTest();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isOutOfBounds(final long value) {
        return value != (value & mask);
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

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    final class UnitTest {
        public int getBits() {
            return bits;
        }
    }
}
