package com.dipasquale.common;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
public interface EnumFactory<T extends Enum<T>> extends Serializable {
    T create();

    static <T extends Enum<T>> EnumFactory<T> createLiteral(final T value) {
        return new EnumFactoryLiteral<>(value);
    }

    static <T extends Enum<T>> EnumFactory<T> createRandom(final RandomSupportFloat randomSupport, final List<T> values) {
        return new EnumFactoryRandom<T>(randomSupport, values);
    }
}
