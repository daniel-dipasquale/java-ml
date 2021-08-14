package com.dipasquale.common.random.float2;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BoundedRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6034184836835887844L;
    private final RandomSupport randomSupport;
    private final double min;
    private final double max;

    @Override
    public double next() {
        return randomSupport.next(min, max);
    }
}
