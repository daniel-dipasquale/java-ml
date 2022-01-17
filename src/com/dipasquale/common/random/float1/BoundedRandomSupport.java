package com.dipasquale.common.random.float1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BoundedRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6034184836835887844L;
    private final RandomSupport randomSupport;
    private final float minimum;
    private final float maximum;

    @Override
    public float next() {
        return randomSupport.next(minimum, maximum);
    }
}
