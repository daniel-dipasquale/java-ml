package com.dipasquale.data.structure.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class MapBase<TKey, TValue> implements MapExtended<TKey, TValue> {
    private final KeySet<TKey, TValue> keySet = new KeySet<>(this);
    private final Values<TKey, TValue> values = new Values<>(this);
    private final EntrySet<TKey, TValue> entrySet = new EntrySet<>(this);

    @Override
    public abstract int size();

    @Override
    public abstract boolean containsKey(Object key);

    @Override
    public boolean containsValue(final Object value) {
        return stream().anyMatch(e -> Objects.equals(e.getValue(), value));
    }

    @Override
    public abstract TValue get(Object key);

    @Override
    public abstract TValue put(TKey key, TValue value);

    @Override
    public abstract TValue remove(Object key);

    @Override
    public abstract void clear();

    @Override
    public Set<TKey> keySet() {
        return keySet;
    }

    @Override
    public Collection<TValue> values() {
        return values;
    }

    @Override
    public Set<Entry<TKey, TValue>> entrySet() {
        return entrySet;
    }

    protected abstract Iterator<? extends Entry<TKey, TValue>> iterator();

    Stream<? extends Entry<TKey, TValue>> stream() {
        Spliterator<Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(iterator(), 0);

        return StreamSupport.stream(entries, false);
    }

    @Override
    public boolean equals(final Object other) {
        return MapExtensions.equals(this, other);
    }

    @Override
    public int hashCode() {
        return MapExtensions.hashCode(this);
    }

    @Override
    public String toString() {
        return MapExtensions.toString(this);
    }
}
