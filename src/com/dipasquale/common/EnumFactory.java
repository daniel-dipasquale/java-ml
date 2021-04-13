package com.dipasquale.common;

import java.io.Serializable;

public interface EnumFactory<T extends Enum<T>> extends Serializable {
    T create();

    EnumFactory<T> selectContended(boolean contended);

    static <T extends Enum<T>> EnumFactory<T> createLiteral(final T value) {
        return new EnumFactoryLiteral<>(value);
    }
}
