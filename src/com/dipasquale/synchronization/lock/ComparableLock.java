package com.dipasquale.synchronization.lock;

import com.dipasquale.common.concurrent.ConcurrentId;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public final class ComparableLock<T extends Comparable<T>> implements Comparable<ComparableLock<T>>, Lock {
    private final Lock lock;
    private final ConcurrentId<T> concurrentId;

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly()
            throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit)
            throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public int compareTo(final ComparableLock<T> comparableLock) {
        return concurrentId.compareTo(comparableLock.concurrentId);
    }

    @Override
    public String toString() {
        return concurrentId.toString();
    }
}
