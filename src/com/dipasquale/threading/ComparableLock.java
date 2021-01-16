package com.dipasquale.threading;

import com.dipasquale.concurrent.ConcurrentId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public static Lock createAggregated(final Iterable<ComparableLock> locks) {
        List<ComparableLock> sortedLocks = StreamSupport.stream(locks.spliterator(), false)
                .sorted()
                .collect(Collectors.toList());

        return new MultiLock(sortedLocks);
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

    final class UnitTest {
        public Lock getLock() {
            return lock;
        }
    }
}
