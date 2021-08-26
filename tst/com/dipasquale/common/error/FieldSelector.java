package com.dipasquale.common.error;

@FunctionalInterface
public interface FieldSelector<T extends Throwable> {
    Object get(T error);
}
