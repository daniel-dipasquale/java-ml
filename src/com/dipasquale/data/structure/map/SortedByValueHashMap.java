package com.dipasquale.data.structure.map;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.data.structure.set.DequeHashSet;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public final class SortedByValueHashMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = 4141456851307327160L;
    @Getter
    private final Storage<TKey, TValue> storage;

    private SortedByValueHashMap(final MapFactory<TKey, TValue> mapFactory, final Comparator<TValue> comparator) {
        super(createEntriesSetFactory(), createEntryStrategyFactory());
        this.storage = new Storage<>(mapFactory.create(), new TreeMap<>(comparator));
    }

    public SortedByValueHashMap(final Comparator<TValue> comparator) {
        this(HashMap::new, comparator);
    }

    public SortedByValueHashMap(final Comparator<TValue> comparator, final int initialCapacity) {
        this(() -> new HashMap<>(initialCapacity), comparator);
    }

    @Override
    public void clear() {
        storage.map.clear();
        storage.navigableMap.clear();
    }

    private static <TKey, TValue> ObjectFactory<DequeSet<Entry<TKey, TValue>>> createEntriesSetFactory() {
        return (ObjectFactory<DequeSet<Entry<TKey, TValue>>> & Serializable) DequeHashSet::new;
    }

    private static <TKey, TValue> EntryStrategyFactory<TKey, TValue> createEntryStrategyFactory() {
        return (EntryStrategyFactory<TKey, TValue> & Serializable) InternalEntry::new;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class InternalEntry<TKey, TValue> implements Entry<TKey, TValue>, Serializable {
        @Serial
        private static final long serialVersionUID = 8675743617156953996L;
        @EqualsAndHashCode.Include
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }
    }
}
