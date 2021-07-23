package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatBiFactoryIllegalState implements FloatBiFactory {
    @Serial
    private static final long serialVersionUID = 4112885393370486526L;
    private final String message;

    @Override
    public float create() {
        throw new IllegalStateException(message);
    }

    @Override
    public FloatBiFactory selectContended(final boolean contended) {
        return this;
    }
}
