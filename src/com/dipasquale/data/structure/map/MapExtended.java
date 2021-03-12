package com.dipasquale.data.structure.map;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface MapExtended<TKey, TValue> extends Map<TKey, TValue> {
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default TValue getOrDefault(final Object key, final TValue defaultValue) {
        TValue value = get(key);

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    @Override
    default void putAll(final Map<? extends TKey, ? extends TValue> other) {
        for (Entry<? extends TKey, ? extends TValue> entry : other.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    default boolean replace(final TKey key, final TValue oldValue, final TValue newValue) {
        Object currentValue = get(key);

        if (currentValue == null || !Objects.equals(currentValue, oldValue)) {
            return false;
        }

        put(key, newValue);

        return true;
    }

    @Override
    default TValue replace(final TKey key, final TValue value) {
        if (containsKey(key)) {
            return put(key, value);
        }

        return null;
    }

    @Override
    default TValue computeIfAbsent(final TKey key, Function<? super TKey, ? extends TValue> mapper) {
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
    default TValue computeIfPresent(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
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
    default TValue compute(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> mapper) {
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
    default TValue merge(final TKey key, final TValue value, final BiFunction<? super TValue, ? super TValue, ? extends TValue> mapper) {
        TValue oldValue = get(key);
        TValue newValue = oldValue == null ? value : mapper.apply(oldValue, value);

        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }

        return newValue;
    }
}
