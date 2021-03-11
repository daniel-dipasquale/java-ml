package com.dipasquale.data.structure.map;

import lombok.RequiredArgsConstructor;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
class KeySet<TKey, TValue> extends AbstractSet<TKey> {
    private final MapBase<TKey, TValue> map;

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

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
    public final boolean removeAll(final Collection<?> keys) {
        long removed = keys.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }

    @Override
    public final boolean retainAll(final Collection<?> keys) {
        Set<?> keysToRetain = ensureSet(keys);

        List<TKey> keysToRemove = StreamSupport.stream(spliterator(), false)
                .filter(k -> !keysToRetain.contains(k))
                .collect(Collectors.toList());

        keysToRemove.forEach(map::remove);

        return !keysToRemove.isEmpty();
    }

    @Override
    public final void clear() {
        map.clear();
    }

    @Override
    public Iterator<TKey> iterator() {
        Spliterator<Map.Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(map.iterator(), 0);

        return StreamSupport.stream(entries, false)
                .map(Map.Entry::getKey)
                .iterator();
    }
}
