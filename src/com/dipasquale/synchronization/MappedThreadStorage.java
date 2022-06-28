package com.dipasquale.synchronization;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;

public final class MappedThreadStorage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1511060697614420651L;
    private final MappedThreadIndex mappedThreadIndex;
    private final T[] mappedThreadValues;

    public MappedThreadStorage(final MappedThreadIndex mappedThreadIndex, final Class<T> type) {
        this.mappedThreadIndex = mappedThreadIndex;
        this.mappedThreadValues = (T[]) Array.newInstance(type, mappedThreadIndex.size());
    }

    private T put(final int index, final T value) {
        T previousValue = mappedThreadValues[index];

        mappedThreadValues[index] = value;

        return previousValue;
    }

    public T put(final T value) {
        int index = mappedThreadIndex.getIndex();

        if (index == MappedThreadIndex.NOT_MAPPED_INDEX) {
            return null;
        }

        return put(index, value);
    }

    public T getOrDefault(final T defaultValue) {
        int index = mappedThreadIndex.getIndex();

        if (index == MappedThreadIndex.NOT_MAPPED_INDEX) {
            return defaultValue;
        }

        T value = mappedThreadValues[index];

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public T get() {
        return getOrDefault(null);
    }

    public void remove() {
        put(null);
    }
}
