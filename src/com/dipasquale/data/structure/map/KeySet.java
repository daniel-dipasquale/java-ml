package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.collection.AbstractCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class KeySet<TKey, TValue> extends AbstractCollection<TKey> implements Set<TKey> {
    private final AbstractMap<TKey, TValue> map;

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
}
