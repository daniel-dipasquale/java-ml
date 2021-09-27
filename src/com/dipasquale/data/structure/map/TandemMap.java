package com.dipasquale.data.structure.map;

import com.google.common.collect.Iterables;

import java.util.Map;

public final class TandemMap<TKey, TValue> extends AbstractMap<TKey, TValue> {
    private final Map<TKey, TValue> first;
    private final Map<TKey, TValue> second;

    public TandemMap(final Map<TKey, TValue> first, final Map<TKey, TValue> second) {
        super(Iterables.concat(first.entrySet(), second.entrySet())::iterator);
        this.first = first;
        this.second = second;
    }

    @Override
    public int size() {
        return first.size() + second.size();
    }

    @Override
    public boolean isEmpty() {
        return first.isEmpty() && second.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return first.containsKey(key) || second.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return first.containsValue(value) || second.containsValue(value);
    }

    @Override
    public TValue get(final Object key) {
        if (first.containsKey(key)) {
            return first.get(key);
        }

        return second.get(key);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        return first.put(key, value);
    }

    @Override
    public TValue remove(final Object key) {
        if (first.containsKey(key)) {
            try {
                return first.remove(key);
            } finally {
                second.remove(key);
            }
        }

        return second.remove(key);
    }

    @Override
    public void putAll(final Map<? extends TKey, ? extends TValue> other) {
        first.putAll(other);
    }

    @Override
    public void clear() {
        first.clear();
        second.clear();
    }
}
