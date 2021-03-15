package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.data.structure.set.HashDequeSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConcurrentSortedByValueMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    private ConcurrentSortedByValueMap(final Map<TKey, Entry<TKey, TValue>> map, final Comparator<TValue> comparator) {
        super(map, new ConcurrentSkipListMap<>(comparator), () -> DequeSet.createSynchronized(new HashDequeSet<>()), EntryInternal::new);
    }

    public ConcurrentSortedByValueMap(final Comparator<TValue> comparator) {
        this(new ConcurrentHashMap<>(), comparator);
    }

    public ConcurrentSortedByValueMap(final Comparator<TValue> comparator, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this(new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel), comparator);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class EntryInternal<TKey, TValue> implements Entry<TKey, TValue> {
        @EqualsAndHashCode.Include
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }
}
