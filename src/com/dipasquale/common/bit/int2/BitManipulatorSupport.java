package com.dipasquale.common.bit.int2;

import com.dipasquale.common.ArgumentValidatorSupport;

public interface BitManipulatorSupport {
    long size();

    boolean isWithinBounds(long value);

    long extract(long raw, long offset);

    long merge(long raw, long offset, long value);

    static BitManipulatorSupport create(final int bits) {
        ArgumentValidatorSupport.ensureGreaterThanZero(bits, "bits");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(bits, 64, "bits");

        if (bits == 64) {
            return new Only64BitManipulatorSupport();
        }

        return new NBitManipulatorSupport(bits);
    }
}
