package com.dipasquale.common;

@FunctionalInterface
public interface EnumFactory<T extends Enum<T>> {
    T create();
}
