package com.dipasquale.data.structure.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractMap<TKey, TValue> implements Map<TKey, TValue> {
    private final KeySet<TKey, TValue> keySet = new KeySet<>(this);
    private final Values<TKey, TValue> values = new Values<>(this);
    private final EntrySet<TKey, TValue> entrySet = new EntrySet<>(this);

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public abstract boolean containsKey(Object key);

    @Override
    public boolean containsValue(final Object value) {
        return stream().anyMatch(e -> Objects.equals(e.getValue(), value));
    }

    @Override
    public abstract TValue get(Object key);

    @Override
    public TValue getOrDefault(final Object key, final TValue defaultValue) {
        TValue value = get(key);

        if (value != null) {
            return value;
        }

        return defaultValue;
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
        Object currentValue = get(key);

        if (currentValue == null || !Objects.equals(currentValue, oldValue)) {
            return false;
        }

        put(key, newValue);

        return true;
    }

    @Override
    public TValue replace(final TKey key, final TValue value) {
        if (containsKey(key)) {
            return put(key, value);
        }

        return null;
    }

    @Override
    public abstract TValue remove(Object key);

    @Override
    public TValue computeIfAbsent(final TKey key, Function<? super TKey, ? extends TValue> mapper) {
        TValue oldValue = get(key);

        if (oldValue == null) {
            TValue newValue = mapper.apply(key);

            if (newValue != null) {
                put(key, newValue);

                return newValue;
            }
        }

        return oldValue;
    }

    @Override
    public TValue computeIfPresent(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
        TValue oldValue = get(key);

        if (oldValue != null) {
            TValue newValue = mapper.apply(key, oldValue);

            if (newValue != null) {
                put(key, newValue);

                return newValue;
            }

            remove(key);
        }

        return null;
    }

    @Override
    public TValue compute(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
        TValue oldValue = get(key);
        TValue newValue = mapper.apply(key, oldValue);

        if (newValue == null) {
            if (oldValue != null) {
                remove(key);
            }

            return null;
        }

        put(key, newValue);

        return newValue;
    }

    @Override
    public TValue merge(final TKey key, final TValue value, final BiFunction<? super TValue, ? super TValue, ? extends TValue> mapper) {
        TValue oldValue = get(key);
        TValue newValue = oldValue == null ? value : mapper.apply(oldValue, value);

        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }

        return newValue;
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

    protected abstract Iterator<? extends Entry<TKey, TValue>> iterator();

    Stream<? extends Entry<TKey, TValue>> stream() {
        Spliterator<Entry<TKey, TValue>> entries = Spliterators.spliteratorUnknownSize(iterator(), 0);

        return StreamSupport.stream(entries, false);
    }

    private boolean equals(final Map<TKey, TValue> other) {
        if (size() != other.size()) {
            return false;
        }

        for (Map.Entry<TKey, TValue> entry : entrySet()) {
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

        if (other instanceof Map) {
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

        for (Map.Entry<TKey, TValue> entry : entrySet()) {
            hashCode += entry.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<TKey, TValue>> iterator = entrySet().iterator();

        if (!iterator.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        int items = 0;

        sb.append('{');

        do {
            if (items++ > 0) {
                sb.append(',');
                sb.append(' ');
            }

            Map.Entry<TKey, TValue> entry = iterator.next();
            TKey key = entry.getKey();
            TValue value = entry.getValue();

            sb.append(key == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
        } while (iterator.hasNext());

        sb.append('}');

        return sb.toString();
    }
}
