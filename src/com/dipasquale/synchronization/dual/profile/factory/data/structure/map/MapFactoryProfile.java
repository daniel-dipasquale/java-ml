package com.dipasquale.synchronization.dual.profile.factory.data.structure.map;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MapFactoryProfile extends AbstractObjectProfile<MapFactory> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3969564038761709933L;

    public MapFactoryProfile(final boolean concurrent, final MapFactory concurrentMapFactory, final MapFactory defaultMapFactory) {
        super(concurrent, concurrentMapFactory, defaultMapFactory);
    }

    public static MapFactoryProfile createHash(final boolean concurrent, final int numberOfThreads, final int initialCapacity) {
        MapFactory concurrentMapFactory = new HashMapFactory(true, numberOfThreads, initialCapacity);
        MapFactory defaultMapFactory = new HashMapFactory(false, numberOfThreads, initialCapacity);

        return new MapFactoryProfile(concurrent, concurrentMapFactory, defaultMapFactory);
    }

    public static MapFactoryProfile createHash(final boolean concurrent, final int numberOfThreads) {
        return createHash(concurrent, numberOfThreads, 16);
    }

    private static <TKey, TValue> Map<TKey, TValue> createMap(final boolean concurrent, final int numberOfThreads, final int initialCapacity, final Map<TKey, TValue> other) {
        if (concurrent) {
            Map<TKey, TValue> map = new ConcurrentHashMap<>(initialCapacity, 0.75f, numberOfThreads);

            if (other != null) {
                map.putAll(other);
            }

            return map;
        }

        if (other == null) {
            return new HashMap<>(initialCapacity);
        }

        return new HashMap<>(other);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class HashMapFactory implements MapFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -4793016071107116770L;
        private final boolean concurrent;
        private final int numberOfThreads;
        private final int initialCapacity;

        @Override
        public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
            return createMap(concurrent, numberOfThreads, initialCapacity, other);
        }
    }
}
