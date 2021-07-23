package com.dipasquale.common.concurrent;

import com.dipasquale.common.EnumFactory;

public interface EnumBiFactory<T extends Enum<T>> extends EnumFactory<T> {
    EnumBiFactory<T> selectContended(boolean contended);

    static <T extends Enum<T>> EnumBiFactory<T> createLiteral(final T value) {
        return new EnumBiFactoryLiteral<>(value);
    }
}
