package com.dipasquale.common.bit;

import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.Getter;

public final class VectorManipulatorSupport {
    private static final VectorManipulatorSupport MAXIMUM_INSTANCE = new VectorManipulatorSupport(32);
    private final int bits;
    private final int integerMask;
    private final long longMask;
    @Getter
    private final int vectorSizePerInteger;
    @Getter
    private final int vectorSizePerLong;

    VectorManipulatorSupport(final int bits) {
        this.bits = bits;
        this.integerMask = (1 << bits) - 1;
        this.longMask = (1L << bits) - 1L;
        this.vectorSizePerInteger = 32 / bits;
        this.vectorSizePerLong = 64 / bits;
    }

    public boolean isWithinBounds(final int value) {
        return value == (value & integerMask);
    }

    public boolean isWithinBounds(final long value) {
        return value == (value & longMask);
    }

    public int extract(final int vector, final int offset) {
        int shifts = offset * bits;

        return (vector >> shifts) & integerMask;
    }

    public long extract(final long vector, final int offset) {
        int shifts = offset * bits;

        return (vector >> shifts) & longMask;
    }

    public int merge(final int vector, final int offset, final int value) {
        int shifts = offset * bits;

        return (vector & ~(integerMask << shifts)) | ((value & integerMask) << shifts);
    }

    public long merge(final long vector, final int offset, final long value) {
        int shifts = offset * bits;

        return (vector & ~(longMask << shifts)) | ((value & longMask) << shifts);
    }

    public static VectorManipulatorSupport create(final int bits) {
        ArgumentValidatorSupport.ensureGreaterThanZero(bits, "bits");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(bits, 32, "bits");

        if (bits == 32) {
            return MAXIMUM_INSTANCE;
        }

        return new VectorManipulatorSupport(bits);
    }
}
