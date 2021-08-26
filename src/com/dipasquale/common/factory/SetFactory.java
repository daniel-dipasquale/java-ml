package com.dipasquale.common.factory;

import java.util.Set;

@FunctionalInterface
public interface SetFactory {
    <T> Set<T> create(Set<T> other);
}
