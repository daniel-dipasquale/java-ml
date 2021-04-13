package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EnumFactoryLiteral<T extends Enum<T>> implements EnumFactory<T> {
    @Serial
    private static final long serialVersionUID = 6776600093764259448L;
    private final T value;

    @Override
    public T create() {
        return value;
    }

    @Override
    public EnumFactory<T> selectContended(final boolean contended) {
        return this;
    }
}
