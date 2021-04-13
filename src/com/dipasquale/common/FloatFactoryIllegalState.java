package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryIllegalState implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 4112885393370486526L;
    private final String message;

    @Override
    public float create() {
        throw new IllegalStateException(message);
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        return this;
    }
}
