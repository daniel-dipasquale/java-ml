package com.dipasquale.threading.lock;

import com.dipasquale.concurrent.ConcurrentId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComparableReadWriteLock implements ReadWriteLock {
    private final ComparableLock readComparableLock;
    private final ComparableLock writeComparableLock;

    public static <T extends Comparable<T>> ComparableReadWriteLock create(final ReadWriteLock lock, final ConcurrentId<T> concurrentId) {
        ComparableLock readComparableLock = ComparableLock.create(lock.readLock(), concurrentId);
        ComparableLock writeComparableLock = ComparableLock.create(lock.writeLock(), concurrentId);

        return new ComparableReadWriteLock(readComparableLock, writeComparableLock);
    }

    @Override
    public ComparableLock readLock() {
        return readComparableLock;
    }

    @Override
    public ComparableLock writeLock() {
        return writeComparableLock;
    }

    @Override
    public String toString() {
        return String.format("read: %s, write: %s", readComparableLock, writeComparableLock);
    }
}
