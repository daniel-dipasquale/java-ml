package com.dipasquale.data.structure.map;

import com.dipasquale.data.structure.collection.AbstractCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MapKeySet<TKey, TValue> extends AbstractCollection<TKey> implements Set<TKey> {
    @Serial
    private static final long serialVersionUID = 6980608787305274843L;
    private final Map<TKey, TValue> map;
    private final IteratorFactory<TKey, TValue> iteratorFactory;

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public final boolean isEmpty() {
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
    public final Iterator<TKey> iterator() {
        return iteratorFactory.createStream()
                .map(Map.Entry::getKey)
                .iterator();
    }
}
