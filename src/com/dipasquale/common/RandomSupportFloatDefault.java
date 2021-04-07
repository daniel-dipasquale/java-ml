package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Random;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomSupportFloatDefault implements RandomSupportFloat {
    @Serial
    private static final long serialVersionUID = -1414752993799776885L;
    private final Random random;

    @Override
    public float next() {
        return random.nextFloat();
    }
}
