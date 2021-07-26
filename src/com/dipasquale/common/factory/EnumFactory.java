package com.dipasquale.common.factory;

@FunctionalInterface
public interface EnumFactory<T extends Enum<T>> {
    T create();
}
