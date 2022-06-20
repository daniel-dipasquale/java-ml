package com.dipasquale.synchronization.dual.mode.data.structure.map;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HashMapFactoryCreator implements DualModeFactory<MapFactory>, Serializable {
    @Serial
    private static final long serialVersionUID = -7796744561536252207L;
    private static final HashMapFactoryCreator INSTANCE = new HashMapFactoryCreator();

    public static HashMapFactoryCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public MapFactory create(final ConcurrencyLevelState concurrencyLevelState) {
        return new HashMapFactory(concurrencyLevelState.getCurrent() > 0, concurrencyLevelState.getMaximum(), 16);
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
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
    }
}
