package com.dipasquale.common.error;

@FunctionalInterface
public interface FieldAccessor<T extends Throwable> {
    Object get(T error);
}
