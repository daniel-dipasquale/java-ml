package com.dipasquale.synchronization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MappedThreadStorage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1511060697614420651L;
    private static final int NOT_MAPPED_INDEX = -1;
    private final Map<Long, Integer> threadIdIndexes;
    private final T[] mappedThreadValues;
    private transient ThreadLocal<UnmappedThread<T>> unmappedThread;

    private static Map<Long, Integer> createThreadIdIndexes(final List<Long> threadIds) {
        Map<Long, Integer> threadIdIndexes = new HashMap<>();

        for (int i = 0, c = threadIds.size(); i < c; i++) {
            threadIdIndexes.put(threadIds.get(i), i);
        }

        return threadIdIndexes;
    }

    public MappedThreadStorage(final Class<T> type, final List<Long> threadIds) {
        this.threadIdIndexes = createThreadIdIndexes(threadIds);
        this.mappedThreadValues = (T[]) Array.newInstance(type, threadIds.size());
        this.unmappedThread = ThreadLocal.withInitial(UnmappedThread::new);
    }

    private int getMappedIndex() {
        return threadIdIndexes.getOrDefault(Thread.currentThread().getId(), NOT_MAPPED_INDEX);
    }

    private T putOnMappedThread(final int index, final T value) {
        T previousValue = mappedThreadValues[index];

        mappedThreadValues[index] = value;

        return previousValue;
    }

    private T putOnUnmappedThread(final T value) {
        UnmappedThread<T> fixedUnmappedThread = unmappedThread.get();
        T previousValue = fixedUnmappedThread.value;

        if (value != null) {
            fixedUnmappedThread.value = value;
        } else {
            unmappedThread.remove();
        }

        return previousValue;
    }

    public T put(final T value) {
        int index = getMappedIndex();

        if (index > NOT_MAPPED_INDEX) {
            return putOnMappedThread(index, value);
        }

        return putOnUnmappedThread(value);
    }

    public T getOrDefault(final T defaultValue) {
        int index = getMappedIndex();

        if (index > NOT_MAPPED_INDEX) {
            T value = mappedThreadValues[index];

            if (value != null) {
                return value;
            }

            return defaultValue;
        }

        T value = unmappedThread.get().value;

        if (value != null) {
            return value;
        }

        unmappedThread.remove();

        return defaultValue;
    }

    public T get() {
        return getOrDefault(null);
    }

    public void remove() {
        int index = getMappedIndex();

        if (index > NOT_MAPPED_INDEX) {
            putOnMappedThread(index, null);
        } else {
            unmappedThread.remove();
        }
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        unmappedThread = ThreadLocal.withInitial(UnmappedThread::new);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class UnmappedThread<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 9087035340614667470L;
        private T value = null;
    }
}
