package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatBiFactoryLiteral implements FloatBiFactory {
    @Serial
    private static final long serialVersionUID = 4181512590387729925L;
    private final float value;

    @Override
    public float create() {
        return value;
    }

    @Override
    public FloatBiFactory selectContended(final boolean contended) {
        return this;
    }
}
