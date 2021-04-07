package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryRandom implements FloatFactory {
    @Serial
    private static final long serialVersionUID = -2347098568911269562L;
    private final RandomSupportFloat randomSupport;
    private final float min;
    private final float max;

    @Override
    public float create() {
        return randomSupport.next(min, max);
    }
}
