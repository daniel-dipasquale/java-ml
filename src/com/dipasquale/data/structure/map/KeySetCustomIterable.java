package com.dipasquale.data.structure.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

final class KeySetCustomIterable<TKey, TValue> extends KeySet<TKey, TValue> { // TODO: consider this as a temporary solution
    private final Iterable<Map.Entry<TKey, TValue>> iterable;

    KeySetCustomIterable(final AbstractMap<TKey, TValue> map, final Iterable<Map.Entry<TKey, TValue>> iterable) {
        super(map);
        this.iterable = iterable;
    }

    @Override
    public Iterator<TKey> iterator() {
        Spliterator<Map.Entry<TKey, TValue>> entries = iterable.spliterator();

        return StreamSupport.stream(entries, false)
                .map(Map.Entry::getKey)
                .iterator();
    }
}
