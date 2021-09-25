package com.dipasquale.common.factory.data.structure.set;

import java.util.Set;

@FunctionalInterface
public interface SetFactory {
    <T> Set<T> create(Set<T> other);
}
