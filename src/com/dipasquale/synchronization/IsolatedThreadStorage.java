package com.dipasquale.synchronization;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class IsolatedThreadStorage<T> implements Iterable<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1511060697614420651L;
    private final IsolatedThreadIndex isolatedThreadIndex;
    private final Object[] isolatedThreadValues;

    public IsolatedThreadStorage(final IsolatedThreadIndex isolatedThreadIndex) {
        this.isolatedThreadIndex = isolatedThreadIndex;
        this.isolatedThreadValues = new Object[isolatedThreadIndex.size()];
    }

    public IsolatedThreadStorage(final Set<Long> threadIds) {
        this(new IsolatedThreadIndex(threadIds));
    }

    public int size() {
        return isolatedThreadValues.length;
    }

    public T getOrDefault(final int index, final T defaultValue) {
        if (index == IsolatedThreadIndex.NOT_MAPPED_INDEX) {
            return defaultValue;
        }

        T value = (T) isolatedThreadValues[index];

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public T fetchOrDefault(final T defaultValue) {
        int index = isolatedThreadIndex.getCurrentIndex();

        return getOrDefault(index, defaultValue);
    }

    public T get(int index) {
        return getOrDefault(index, null);
    }

    public T fetch() {
        int index = isolatedThreadIndex.getCurrentIndex();

        return get(index);
    }

    public T put(final int index, final T value) {
        T previousValue = (T) isolatedThreadValues[index];

        isolatedThreadValues[index] = value;

        return previousValue;
    }

    public T attach(final T value) {
        int index = isolatedThreadIndex.getCurrentIndex();

        if (index == IsolatedThreadIndex.NOT_MAPPED_INDEX) {
            return null;
        }

        return put(index, value);
    }

    public void remove(final int index) {
        put(index, null);
    }

    public void detach() {
        int index = isolatedThreadIndex.getCurrentIndex();

        remove(index);
    }

    public T computeIfAbsent(final Supplier<T> supplier) {
        int index = isolatedThreadIndex.getCurrentIndex();
        T oldValue = (T) isolatedThreadValues[index];

        if (oldValue == null) {
            T newValue = supplier.get();

            if (newValue != null) {
                isolatedThreadValues[index] = newValue;

                return newValue;
            }
        }

        return oldValue;
    }

    public void clear() {
        Arrays.fill(isolatedThreadValues, null);
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.stream(isolatedThreadValues)
                .filter(Objects::nonNull)
                .map(value -> (T) value)
                .iterator();
    }
}
