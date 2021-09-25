package com.dipasquale.common.factory.data.structure.map;

import java.util.Map;

@FunctionalInterface
public interface MapFactory {
    <TKey, TValue> Map<TKey, TValue> create(Map<TKey, TValue> other);

    default <TKey, TValue> Map<TKey, TValue> create() {
        return create(null);
    }
}
