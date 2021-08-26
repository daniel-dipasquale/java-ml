package com.dipasquale.synchronization.lock;

import com.dipasquale.common.concurrent.ConcurrentId;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class ComparableReadWriteLock<T extends Comparable<T>> implements ReadWriteLock {
    private final ComparableLock<T> readComparableLock;
    private final ComparableLock<T> writeComparableLock;

    public ComparableReadWriteLock(final ReadWriteLock lock, final ConcurrentId<T> concurrentId) {
        this.readComparableLock = new ComparableLock<>(lock.readLock(), concurrentId);
        this.writeComparableLock = new ComparableLock<>(lock.writeLock(), concurrentId);
    }

    @Override
    public Lock readLock() {
        return readComparableLock;
    }

    @Override
    public Lock writeLock() {
        return writeComparableLock;
    }

    @Override
    public String toString() {
        return String.format("read: %s, write: %s", readComparableLock, writeComparableLock);
    }
}
