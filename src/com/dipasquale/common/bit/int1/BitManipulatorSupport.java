package com.dipasquale.common.bit.int1;

import com.dipasquale.common.ArgumentValidatorSupport;

public interface BitManipulatorSupport {
    int size();

    boolean isWithinBounds(int value);

    int extract(int raw, int offset);

    int merge(int raw, int offset, int value);

    static BitManipulatorSupport create(final int bits) {
        ArgumentValidatorSupport.ensureGreaterThanZero(bits, "bits");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(bits, 32, "bits");

        if (bits == 32) {
            return new Only32BitManipulatorSupport();
        }

        return new NBitManipulatorSupport(bits);
    }
}
