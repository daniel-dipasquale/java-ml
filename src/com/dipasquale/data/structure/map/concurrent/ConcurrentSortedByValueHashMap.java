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

public final class ConcurrentSortedByValueHashMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> { // TODO: the default interface implementation isn't concurrent safe
    @Serial
    private static final long serialVersionUID = -2007543205420858049L;
    private final MapFactory<TKey, TValue> mapFactory;
    private final NavigableMapFactory<TKey, TValue> navigableMapFactory;
    @Getter
    private volatile Storage<TKey, TValue> storage;

    private ConcurrentSortedByValueHashMap(final ObjectFactory<DequeSet<Entry<TKey, TValue>>> entriesSetFactory, final MapFactory<TKey, TValue> mapFactory, final NavigableMapFactory<TKey, TValue> navigableMapFactory) {
        super(entriesSetFactory, createEntryStrategyFactory());
        this.mapFactory = mapFactory;
        this.navigableMapFactory = navigableMapFactory;
        this.storage = new Storage<>(mapFactory.create(), navigableMapFactory.create());
    }

    private ConcurrentSortedByValueHashMap(final MapFactory<TKey, TValue> mapFactory, final Comparator<TValue> comparator) {
        this(createEntriesSetFactory(), mapFactory, (NavigableMapFactory<TKey, TValue> & Serializable) () -> new ConcurrentSkipListMap<>(comparator));
    }

    public ConcurrentSortedByValueHashMap(final Comparator<TValue> comparator) {
        this((MapFactory<TKey, TValue> & Serializable) ConcurrentHashMap::new, comparator);
    }

    public ConcurrentSortedByValueHashMap(final Comparator<TValue> comparator, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this((MapFactory<TKey, TValue> & Serializable) () -> new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel), comparator);
    }

    @Override
    public void clear() {
        storage = new Storage<>(mapFactory.create(), navigableMapFactory.create());
    }

    private static <TKey, TValue> ObjectFactory<DequeSet<Entry<TKey, TValue>>> createEntriesSetFactory() {
        return (ObjectFactory<DequeSet<Entry<TKey, TValue>>> & Serializable) () -> DequeSet.createSynchronized(new DequeHashSet<>());
    }

    private static <TKey, TValue> EntryStrategyFactory<TKey, TValue> createEntryStrategyFactory() {
        return (EntryStrategyFactory<TKey, TValue> & Serializable) EntryInternal::new;
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
