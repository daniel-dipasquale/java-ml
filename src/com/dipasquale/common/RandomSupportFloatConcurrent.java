package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomSupportFloatConcurrent implements RandomSupportFloat {
    @Serial
    private static final long serialVersionUID = -159029299951284958L;

    @Override
    public float next() {
        return ThreadLocalRandom.current().nextFloat();
    }
}
