package com.dipasquale.data.structure.map;

import java.io.Serial;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

final class MapKeySetProxyIterable<TKey, TValue> extends MapKeySet<TKey, TValue> {
    @Serial
    private static final long serialVersionUID = -3698314720895098998L; // TODO: consider this as a temporary solution
    private final Iterable<Map.Entry<TKey, TValue>> iterable;

    MapKeySetProxyIterable(final AbstractMap<TKey, TValue> map, final Iterable<Map.Entry<TKey, TValue>> iterable) {
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
