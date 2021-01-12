package com.dipasquale.data.structure.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class MapBase<TKey, TValue> extends AbstractMap<TKey, TValue> {
    private final KeySet<TKey, TValue> keySet = new KeySet<>(this, this::iterator);
    private final Values<TKey, TValue> values = new Values<>(this, this::iterator);
    private final EntrySet<TKey, TValue> entrySet = new EntrySet<>(this, this::iterator);

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

    protected abstract PutChange<? extends Entry<TKey, TValue>> putEntry(TKey key, TValue value);

    @Override
    public final TValue put(final TKey key, final TValue value) {
        return putEntry(key, value).oldValue;
    }

    protected abstract Entry<TKey, TValue> removeEntry(TKey key);

    @Override
    public final TValue remove(final Object key) {
        Entry<TKey, TValue> entry = removeEntry((TKey) key);

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

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

    protected abstract Iterator<Entry<TKey, TValue>> iterator();

    @RequiredArgsConstructor
    @Getter
    protected class PutChange<TEntry extends Entry<TKey, TValue>> {
        private final TEntry entry;
        private final TValue oldValue;
        private final boolean isNew;
    }
}
