package com.dipasquale.common.factory;

import java.util.Map;

@FunctionalInterface
public interface MapFactory {
    <TKey, TValue> Map<TKey, TValue> create(Map<TKey, TValue> other);
}
