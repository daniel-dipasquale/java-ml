package com.dipasquale.common.factory;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class LiteralEnumFactory<T extends Enum<T>> implements EnumFactory<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -6364064485626731510L;
    private final T value;

    @Override
    public T create() {
        return value;
    }
}
