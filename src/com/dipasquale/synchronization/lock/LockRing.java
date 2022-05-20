package com.dipasquale.synchronization.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface LockRing<TKey, TLock> {
    TLock createLock(TKey key);

    TKey identify(TLock lock);

    TLock awaitAnyUnlocked() throws InterruptedException;

    TLock awaitAnyUnlocked(long timeout, TimeUnit unit) throws InterruptedException;

    static <T extends Comparable<T>> LockRing<T, Lock> createStandard() {
        IdentityKeyDispatcher<T> keyDispatcher = new IdentityKeyDispatcher<>();

        return new InternalLockRing<>(keyDispatcher);
    }

    static <T> LockRing<T, Lock> createInsertionOrder(final int numberOfThreads) {
        InsertionOrderKeyDispatcher<T> keyDispatcher = new InsertionOrderKeyDispatcher<>(numberOfThreads);

        return new InternalLockRing<>(keyDispatcher);
    }

    static <T extends Comparable<T>> LockRing<T, ReadWriteLock> createStandard(final boolean fair) {
        IdentityKeyDispatcher<T> keyDispatcher = new IdentityKeyDispatcher<>();

        return new InternalReadWriteLockRing<>(keyDispatcher, fair);
    }

    static <T> LockRing<T, ReadWriteLock> createInsertionOrder(final boolean fair, final int numberOfThreads) {
        InsertionOrderKeyDispatcher<T> keyDispatcher = new InsertionOrderKeyDispatcher<>(numberOfThreads);

        return new InternalReadWriteLockRing<>(keyDispatcher, fair);
    }
}
