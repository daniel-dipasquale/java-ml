package com.dipasquale.data.structure.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractMap<TKey, TValue> implements Map<TKey, TValue>, Serializable {
    @Serial
    private static final long serialVersionUID = 152413418451406722L;
    private final IteratorFactory<TKey, TValue> iteratorFactory;
    private final Set<TKey> keySet;
    private final Collection<TValue> values;
    private final Set<Entry<TKey, TValue>> entrySet;

    protected AbstractMap(final IteratorFactory<TKey, TValue> iteratorFactory) {
        this.iteratorFactory = iteratorFactory;
        this.keySet = new MapKeySet<>(this, iteratorFactory);
        this.values = new MapValues<>(this, iteratorFactory);
        this.entrySet = new MapEntrySet<>(this, iteratorFactory);
    }

    @Override
    public final Set<TKey> keySet() {
        return keySet;
    }

    @Override
    public final Collection<TValue> values() {
        return values;
    }

    @Override
    public final Set<Entry<TKey, TValue>> entrySet() {
        return entrySet;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public abstract boolean containsKey(Object key);

    @Override
    public boolean containsValue(final Object value) {
        return IteratorFactory.stream(iteratorFactory).anyMatch(e -> Objects.equals(e.getValue(), value));
    }

    @Override
    public abstract TValue get(Object key);

    @Override
    public TValue getOrDefault(final Object key, final TValue defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    public abstract TValue put(TKey key, TValue value);

    @Override
    public void putAll(final Map<? extends TKey, ? extends TValue> other) {
        for (Entry<? extends TKey, ? extends TValue> entry : other.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean replace(final TKey key, final TValue oldValue, final TValue newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    public TValue replace(final TKey key, final TValue value) {
        return Map.super.replace(key, value);
    }

    @Override
    public abstract TValue remove(Object key);

    @Override
    public TValue computeIfAbsent(final TKey key, Function<? super TKey, ? extends TValue> mapper) {
        return Map.super.computeIfAbsent(key, mapper);
    }

    @Override
    public TValue computeIfPresent(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
        return Map.super.computeIfPresent(key, mapper);
    }

    @Override
    public TValue compute(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
        return Map.super.compute(key, mapper);
    }

    @Override
    public TValue merge(final TKey key, final TValue value, final BiFunction<? super TValue, ? super TValue, ? extends TValue> mapper) {
        return Map.super.merge(key, value, mapper);
    }

    @Override
    public abstract void clear();

    private boolean equals(final Map<TKey, TValue> other) {
        if (size() != other.size()) {
            return false;
        }

        for (Entry<TKey, TValue> entry : entrySet()) {
            TKey key = entry.getKey();
            TValue value = entry.getValue();

            if (!Objects.equals(value, other.get(key))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Map<?, ?>) {
            try {
                return equals((Map<TKey, TValue>) other);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        for (Entry<TKey, TValue> entry : entrySet()) {
            hashCode += entry.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        Iterator<Entry<TKey, TValue>> iterator = iteratorFactory.iterator();

        if (!iterator.hasNext()) {
            return "{}";
        }

        StringBuilder stringBuilder = new StringBuilder();
        int items = 0;

        stringBuilder.append('{');

        do {
            if (items++ > 0) {
                stringBuilder.append(',');
                stringBuilder.append(' ');
            }

            Entry<TKey, TValue> entry = iterator.next();
            TKey key = entry.getKey();
            TValue value = entry.getValue();

            stringBuilder.append(key == this ? "(this Map)" : key);
            stringBuilder.append('=');
            stringBuilder.append(value == this ? "(this Map)" : value);
        } while (iterator.hasNext());

        stringBuilder.append('}');

        return stringBuilder.toString();
    }
}
