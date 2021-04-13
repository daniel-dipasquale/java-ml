package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FloatFactoryLiteral implements FloatFactory {
    @Serial
    private static final long serialVersionUID = 4181512590387729925L;
    private final float value;

    @Override
    public float create() {
        return value;
    }

    @Override
    public FloatFactory selectContended(final boolean contended) {
        return this;
    }
}
