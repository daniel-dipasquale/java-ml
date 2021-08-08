package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.set.DequeHashSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class SortedByValueHashMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = 4141456851307327160L;

    private SortedByValueHashMap(final Map<TKey, Entry<TKey, TValue>> map, final Comparator<TValue> comparator) {
        super(map, new TreeMap<>(comparator), DequeHashSet::new, EntryInternal::new);
    }

    public SortedByValueHashMap(final Comparator<TValue> comparator) {
        this(new HashMap<>(), comparator);
    }

    public SortedByValueHashMap(final Comparator<TValue> comparator, final int initialCapacity) {
        this(new HashMap<>(initialCapacity), comparator);
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
