package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface EnumFactory<T extends Enum<T>> extends Serializable {
    T create();

    static <T extends Enum<T>> EnumFactory<T> createLiteral(final T value) {
        return () -> value;
    }
}
