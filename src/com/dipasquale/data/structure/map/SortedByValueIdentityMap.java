package com.dipasquale.data.structure.map;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.data.structure.set.DequeHashSet;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.TreeMap;

public final class SortedByValueIdentityMap<TKey, TValue> extends AbstractSortedByValueMap<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = 7505976089668954036L;
    @Getter
    private final Storage<TKey, TValue> storage;

    private SortedByValueIdentityMap(final MapFactory<TKey, TValue> mapFactory, final Comparator<TValue> comparator) {
        super((ObjectFactory<DequeSet<Entry<TKey, TValue>>> & Serializable) DequeHashSet::new, EntryInternal::new);
        this.storage = createStorage(mapFactory, () -> new TreeMap<>(comparator));
    }

    public SortedByValueIdentityMap(final Comparator<TValue> comparator) {
        this(IdentityHashMap::new, comparator);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private static final class EntryInternal<TKey, TValue> implements Entry<TKey, TValue>, Serializable {
        @Serial
        private static final long serialVersionUID = -5791877901698746014L;
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
