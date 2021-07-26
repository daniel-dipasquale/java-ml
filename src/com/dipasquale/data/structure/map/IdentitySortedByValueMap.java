package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.set.HashDequeSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class IdentitySortedByValueMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = 7505976089668954036L;

    private IdentitySortedByValueMap(final Map<TKey, Entry<TKey, TValue>> map, final Comparator<TValue> comparator) {
        super(map, new TreeMap<>(comparator), HashDequeSet::new, EntryInternal::new);
    }

    public IdentitySortedByValueMap(final Comparator<TValue> comparator) {
        this(new IdentityHashMap<>(), comparator);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static final class EntryInternal<TKey, TValue> implements Entry<TKey, TValue> {
        private final TKey key;
        private final TValue value;

        @Override
        public TValue setValue(final TValue value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(final Object obj) {
            return key == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(key);
        }
    }
}
