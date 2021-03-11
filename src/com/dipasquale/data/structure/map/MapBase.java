package com.dipasquale.data.structure.map;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class MapBase<TKey, TValue> extends AbstractMap<TKey, TValue> {
    private final KeySet<TKey, TValue> keySet = new KeySet<>(this);
    private final Values<TKey, TValue> values = new Values<>(this);
    private final EntrySet<TKey, TValue> entrySet = new EntrySet<>(this);

    @Override
    public abstract int size();

    @Override
    public abstract boolean containsKey(Object key);

    @Override
    public boolean containsValue(final Object value) {
        return values().contains(value);
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
}
