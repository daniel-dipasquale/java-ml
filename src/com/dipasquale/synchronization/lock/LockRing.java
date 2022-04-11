package com.dipasquale.synchronization.lock;

import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.ReusableCountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitCondition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
public final class LockRing<T extends Comparable<T>> {
    private final NavigableMap<T, RegisteredLock> unlockedLocks = new ConcurrentSkipListMap<>();
    private final ReusableCountDownWaitHandle unlockedLocks_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_ZERO);

    private RegisteredLock createLock(final T key, final Lock lock) {
        RegisteredLock registeredLock = new RegisteredLock(key, lock);

        unlockedLocks.put(key, registeredLock);
        unlockedLocks_waitHandle.countUp();

        return registeredLock;
    }

    public Lock createLock(final T key) {
        return createLock(key, new ReentrantLock());
    }

    public ReadWriteLock createReadWriteLock(final T readerKey, final T writerKey, final boolean fair) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock(fair);
        RegisteredLock readLock = createLock(readerKey, readWriteLock.readLock());
        RegisteredLock writeLock = createLock(writerKey, readWriteLock.writeLock());

        return new RegisteredReadWriteLock(readLock, writeLock);
    }

    public ReadWriteLock createReadWriteLock(final T readerKey, final T writerKey) {
        return createReadWriteLock(readerKey, writerKey, false);
    }

    public Lock awaitUnlocked()
            throws InterruptedException {
        while (true) {
            unlockedLocks_waitHandle.await();

            Map.Entry<T, RegisteredLock> lockEntry = unlockedLocks.firstEntry();

            if (lockEntry != null) {
                return lockEntry.getValue();
            }
        }
    }

    public T identify(final Lock lock) {
        RegisteredLock registeredLock = (RegisteredLock) lock;

        return registeredLock.key;
    }

    private Boolean lock(final T key, final Lock wrapperLock, final LockHandler lockHandler)
            throws InterruptedException {
        Boolean result;

        if (wrapperLock.tryLock()) {
            try {
                unlockedLocks_waitHandle.countDown();
                unlockedLocks.remove(key);
                result = lockHandler.invoke();
            } finally {
                wrapperLock.unlock();
            }
        } else {
            wrapperLock.lock();

            try {
                result = lockHandler.invoke();
            } finally {
                wrapperLock.unlock();
            }
        }

        return result;
    }

    private void unlock(final T key, final RegisteredLock registeredLock, final Lock wrapperLock, final Lock underlyingLock) {
        if (wrapperLock.tryLock()) {
            try {
                unlockedLocks.put(key, registeredLock);
                unlockedLocks_waitHandle.countUp();
                underlyingLock.unlock();
            } finally {
                wrapperLock.unlock();
            }
        } else {
            underlyingLock.unlock();
        }
    }

    @FunctionalInterface
    private interface LockHandler {
        Boolean invoke() throws InterruptedException;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class RegisteredLock implements Lock {
        private final T key;
        private final Lock wrapperLock = new ReentrantLock();
        private final Lock underlyingLock;

        @Override
        public void lock() {
            try {
                LockRing.this.lock(key, wrapperLock, () -> {
                    underlyingLock.lock();

                    return null;
                });
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException("underlying lock was interrupted", e);
            }
        }

        @Override
        public void lockInterruptibly()
                throws InterruptedException {
            LockRing.this.lock(key, wrapperLock, () -> {
                underlyingLock.lockInterruptibly();

                return null;
            });
        }

        @Override
        public boolean tryLock() {
            try {
                return LockRing.this.lock(key, wrapperLock, underlyingLock::tryLock);
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException("underlying lock was interrupted", e);
            }
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            return LockRing.this.lock(key, wrapperLock, () -> underlyingLock.tryLock(time, unit));
        }

        @Override
        public void unlock() {
            LockRing.this.unlock(key, this, wrapperLock, underlyingLock);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RegisteredReadWriteLock implements ReadWriteLock {
        private final Lock readLock;
        private final Lock writeLock;

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
