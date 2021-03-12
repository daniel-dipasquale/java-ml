package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.collection.CollectionExtensions;
import com.dipasquale.data.structure.set.SetExtended;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class KeySet<TKey, TValue> implements SetExtended<TKey> {
    private final MapBase<TKey, TValue> map;

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public final boolean contains(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public final boolean add(final TKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(final Object key) {
        return map.remove(key) != null;
    }

    @Override
    public final void clear() {
        map.clear();
    }

    @Override
    public Iterator<TKey> iterator() {
        return map.stream()
                .map(Map.Entry::getKey)
                .iterator();
    }

    @Override
    public boolean equals(final Object other) {
        return CollectionExtensions.equals(this, other);
    }

    @Override
    public int hashCode() {
        return CollectionExtensions.hashCode(this);
    }

    @Override
    public String toString() {
        return CollectionExtensions.toString(this);
    }
}
