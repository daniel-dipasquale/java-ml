package com.dipasquale.common.bit.int1;

final class DefaultBitManipulatorSupport implements BitManipulatorSupport {
    private final int bits;
    private final int mask;
    private final int size;

    DefaultBitManipulatorSupport(final int bits) {
        this.bits = bits;
        this.mask = (1 << bits) - 1;
        this.size = BitManipulatorSupport.MAXIMUM_BITS / bits;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isOutOfBounds(final int value) {
        return value != (value & mask);
    }

    @Override
    public int extract(final int raw, final int offset) {
        int shifts = offset * bits;

        return (raw >> shifts) & mask;
    }

    @Override
    public int merge(final int raw, final int offset, final int value) {
        int shifts = offset * bits;
        int valueCapped = value & mask;
        int maskShifted = mask << shifts;
        int maskShiftedReversed = ~maskShifted;
        int rawCleared = raw & maskShiftedReversed;
        int valueCappedShifted = valueCapped << shifts;

        return rawCleared | valueCappedShifted;
    }
}
