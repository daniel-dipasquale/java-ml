package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InternalReadWriteLockRing<TInternalKey extends Comparable<TInternalKey>, TPublicKey> extends AbstractLockRing<TInternalKey, ReadWriteLock> implements LockRing<TPublicKey, ReadWriteLock> {
    @Serial
    private static final long serialVersionUID = 6340668468791661269L;
    private final KeyDispatcher<TInternalKey, TPublicKey> keyDispatcher;
    private final boolean fair;

    @Override
    public ReadWriteLock createLock(final TPublicKey key) {
        TInternalKey internalKey = keyDispatcher.dispatch(key);
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(fair);
        Lock readLock = readWriteLock.readLock();
        RegisteredLock<TInternalKey, ReadWriteLock> writeLock = createLock(internalKey, readWriteLock.writeLock(), readWriteLock);

        return new RegisteredReadWriteLock<>(readLock, writeLock);
    }

    @Override
    public TPublicKey identify(final ReadWriteLock readWriteLock) {
        RegisteredReadWriteLock<TInternalKey> registeredReadWriteLock = (RegisteredReadWriteLock<TInternalKey>) readWriteLock;
        TInternalKey internalKey = registeredReadWriteLock.writeLock.getKey();

        return keyDispatcher.recall(internalKey);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RegisteredReadWriteLock<T extends Comparable<T>> implements ReadWriteLock, Serializable {
        @Serial
        private static final long serialVersionUID = -3807339724750384827L;
        private final Lock readLock;
        private final RegisteredLock<T, ReadWriteLock> writeLock;

        @Override
        public Lock readLock() {
            return readLock;
        }

        @Override
        public Lock writeLock() {
            return writeLock;
        }
    }
}
