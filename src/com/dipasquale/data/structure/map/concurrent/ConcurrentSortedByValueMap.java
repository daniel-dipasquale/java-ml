package com.dipasquale.data.structure.map.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.data.structure.map.AbstractSortedByValueMap;
import com.dipasquale.data.structure.set.DequeHashSet;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConcurrentSortedByValueMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> { // TODO: the default interface implementation isn't concurrent safe
    @Serial
    private static final long serialVersionUID = -2007543205420858049L;
    private final MapFactory<TKey, TValue> mapFactory;
    private final NavigableMapFactory<TKey, TValue> navigableMapFactory;
    @Getter
    private volatile Storage<TKey, TValue> storage;

    private ConcurrentSortedByValueMap(final MapFactory<TKey, TValue> mapFactory, final Comparator<TValue> comparator) {
        super((ObjectFactory<DequeSet<Entry<TKey, TValue>>> & Serializable) () -> DequeSet.createSynchronized(new DequeHashSet<>()), EntryInternal::new);

        NavigableMapFactory<TKey, TValue> navigableMapFactory = () -> new ConcurrentSkipListMap<>(comparator);

        this.mapFactory = mapFactory;
        this.navigableMapFactory = navigableMapFactory;
        this.storage = createStorage(mapFactory, navigableMapFactory);
    }

    public ConcurrentSortedByValueMap(final Comparator<TValue> comparator) {
        this(ConcurrentHashMap::new, comparator);
    }

    public ConcurrentSortedByValueMap(final Comparator<TValue> comparator, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this(() -> new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel), comparator);
    }

    @Override
    public void clear() {
        storage = createStorage(mapFactory, navigableMapFactory);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class EntryInternal<TKey, TValue> implements Entry<TKey, TValue>, Serializable {
        @Serial
        private static final long serialVersionUID = 3861874265900483685L;
        @EqualsAndHashCode.Include
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }
}
