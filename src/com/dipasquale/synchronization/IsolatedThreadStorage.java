package com.dipasquale.synchronization;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;

public final class IsolatedThreadStorage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1511060697614420651L;
    private final IsolatedThreadIndex isolatedThreadIndex;
    private final T[] isolatedThreadValues;

    public IsolatedThreadStorage(final IsolatedThreadIndex isolatedThreadIndex, final Class<T> type) {
        this.isolatedThreadIndex = isolatedThreadIndex;
        this.isolatedThreadValues = (T[]) Array.newInstance(type, isolatedThreadIndex.size());
    }

    public T put(final int index, final T value) {
        T previousValue = isolatedThreadValues[index];

        isolatedThreadValues[index] = value;

        return previousValue;
    }

    public T putInCurrent(final T value) {
        int index = isolatedThreadIndex.getCurrentIndex();

        if (index == IsolatedThreadIndex.NOT_MAPPED_INDEX) {
            return null;
        }

        return put(index, value);
    }

    public T getOrDefault(final int index, final T defaultValue) {
        if (index == IsolatedThreadIndex.NOT_MAPPED_INDEX) {
            return defaultValue;
        }

        T value = isolatedThreadValues[index];

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public T getFromCurrentOrDefault(final T defaultValue) {
        int index = isolatedThreadIndex.getCurrentIndex();

        return getOrDefault(index, defaultValue);
    }

    public T get(int index) {
        return getOrDefault(index, null);
    }

    public T getFromCurrent() {
        int index = isolatedThreadIndex.getCurrentIndex();

        return get(index);
    }

    public void remove(final int index) {
        put(index, null);
    }

    public void removeFromCurrent() {
        int index = isolatedThreadIndex.getCurrentIndex();

        remove(index);
    }
}
