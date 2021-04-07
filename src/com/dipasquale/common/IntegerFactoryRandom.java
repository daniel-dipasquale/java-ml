package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IntegerFactoryRandom implements IntegerFactory {
    @Serial
    private static final long serialVersionUID = 5726671032773773943L;
    private final RandomSupportFloat randomSupport;
    private final int min;
    private final int max;

    @Override
    public int create() {
        return randomSupport.next(min, max);
    }
}
