package com.dipasquale.common.random.float1;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class BoundedRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6034184836835887844L;
    private final RandomSupport randomSupport;
    private final float min;
    private final float max;

    @Override
    public float next() {
        return randomSupport.next(min, max);
    }
}
