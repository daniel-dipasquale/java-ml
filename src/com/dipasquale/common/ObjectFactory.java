package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface ObjectFactory<T> extends Serializable {
    T create();

    static <T> ObjectFactory<T> createLiteral(final T value) {
        return () -> value;
    }
}
