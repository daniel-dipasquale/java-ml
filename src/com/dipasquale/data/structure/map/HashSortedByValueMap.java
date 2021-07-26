package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.set.HashDequeSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class HashSortedByValueMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = 4141456851307327160L;

    private HashSortedByValueMap(final Map<TKey, Entry<TKey, TValue>> map, final Comparator<TValue> comparator) {
        super(map, new TreeMap<>(comparator), HashDequeSet::new, EntryInternal::new);
    }

    public HashSortedByValueMap(final Comparator<TValue> comparator) {
        this(new HashMap<>(), comparator);
    }

    public HashSortedByValueMap(final Comparator<TValue> comparator, final int initialCapacity) {
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
