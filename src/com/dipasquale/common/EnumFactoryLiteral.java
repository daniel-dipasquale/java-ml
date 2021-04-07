package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EnumFactoryLiteral<T extends Enum<T>> implements EnumFactory<T> {
    @Serial
    private static final long serialVersionUID = 662268060170225535L;
    private final T value;

    @Override
    public T create() {
        return value;
    }
}
