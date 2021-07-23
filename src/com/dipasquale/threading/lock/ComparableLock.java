package com.dipasquale.threading.lock;

import com.dipasquale.common.concurrent.ConcurrentId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComparableLock implements Comparable<ComparableLock>, Lock {
    private final Lock lock;
    private final Comparable<Object> concurrentId;
    @Getter(AccessLevel.PACKAGE)
    private final UnitTest unitTest = new UnitTest();

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    public static <T extends Comparable<T>> ComparableLock create(final Lock lock, final ConcurrentId<T> concurrentId) {
        return new ComparableLock(lock, ensureType(concurrentId));
    }

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
    public int compareTo(final ComparableLock comparableLock) {
        return concurrentId.compareTo(comparableLock.concurrentId);
    }

    @Override
    public String toString() {
        return concurrentId.toString();
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    final class UnitTest {
        public Lock getLock() {
            return lock;
        }
    }
}
