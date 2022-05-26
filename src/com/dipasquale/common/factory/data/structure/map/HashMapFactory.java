package com.dipasquale.common.factory.data.structure.map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HashMapFactory implements MapFactory {
    private static final HashMapFactory INSTANCE = new HashMapFactory();

    public static HashMapFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
        if (other == null) {
            return new HashMap<>();
        }

        return new HashMap<>(other);
    }
}
