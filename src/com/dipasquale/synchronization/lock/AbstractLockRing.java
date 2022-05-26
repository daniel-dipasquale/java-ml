package com.dipasquale.synchronization.lock;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.NanosecondsDateTimeSupport;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.ReusableCountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitCondition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractLockRing<TKey extends Comparable<TKey>, TLock> implements Serializable { // TODO: remove
    @Serial
    private static final long serialVersionUID = 6679248328057781835L;
    private static final DateTimeSupport DATE_TIME_SUPPORT = new NanosecondsDateTimeSupport();
    private final NavigableMap<TKey, RegisteredLock<TKey, TLock>> unlockedLocks = new ConcurrentSkipListMap<>(Comparator.naturalOrder());
    private final ReusableCountDownWaitHandle unlockedLocks_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_ZERO);

    protected RegisteredLock<TKey, TLock> createLock(final TKey key, final Lock underlyingLock, final TLock actualLock) {
        RegisteredLock<TKey, TLock> registeredLock = new RegisteredLock<>(this, key, underlyingLock, actualLock);

        unlockedLocks.put(key, registeredLock);
        unlockedLocks_waitHandle.countUp();

        return registeredLock;
    }

    public TLock awaitAnyUnlocked()
            throws InterruptedException {
        while (true) {
            unlockedLocks_waitHandle.await();

            Map.Entry<TKey, RegisteredLock<TKey, TLock>> lockEntry = unlockedLocks.firstEntry();

            if (lockEntry != null) {
                return lockEntry.getValue().actualLock;
            }
        }
    }

    public TLock awaitAnyUnlocked(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        long offsetDateTime = DATE_TIME_SUPPORT.now();
        long timeoutRemaining = DATE_TIME_SUPPORT.timeUnit().convert(timeout, unit);

        while (true) {
            if (!unlockedLocks_waitHandle.await(timeoutRemaining, DATE_TIME_SUPPORT.timeUnit())) {
                return null;
            }

            Map.Entry<TKey, RegisteredLock<TKey, TLock>> lockEntry = unlockedLocks.firstEntry();

            if (lockEntry != null) {
                return lockEntry.getValue().actualLock;
            }

            long currentDateTime = DATE_TIME_SUPPORT.now();

            timeoutRemaining -= currentDateTime - offsetDateTime;
            offsetDateTime = currentDateTime;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected static final class RegisteredLock<TKey extends Comparable<TKey>, TLock> implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = -8154738479719064226L;
        private final WeakReference<AbstractLockRing<TKey, TLock>> owner;
        private final Lock ownerLock;
        @Getter
        private final TKey key;
        private final Lock underlyingLock;
        private final ReusableCountDownWaitHandle underlyingLock_waitHandle;
        private final TLock actualLock;

        private RegisteredLock(final AbstractLockRing<TKey, TLock> owner, final TKey key, final Lock underlyingLock, final TLock actualLock) {
            this.owner = new WeakReference<>(owner);
            this.ownerLock = new ReentrantLock();
            this.key = key;
            this.underlyingLock = underlyingLock;
            this.underlyingLock_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_NOT_ZERO);
            this.actualLock = actualLock;
        }

        private boolean tryLocking() {
            ownerLock.lock();

            try {
                AbstractLockRing<TKey, TLock> fixedOwner = owner.get();

                fixedOwner.unlockedLocks_waitHandle.countDown();
                fixedOwner.unlockedLocks.remove(key);

                if (!underlyingLock.tryLock()) {
                    return false;
                }

                underlyingLock_waitHandle.countUp();

                return true;
            } finally {
                ownerLock.unlock();
            }
        }

        private void lockInternal()
                throws InterruptedException {
            LockMimicSupport.lock(tryLocking(), underlyingLock_waitHandle, underlyingLock);
        }

        @Override
        public void lock() {
            try {
                lockInternal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new InterruptedRuntimeException("unable to acquire the underlying lock, the current thread was interrupted", e);
            }
        }

        @Override
        public void lockInterruptibly()
                throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            lockInternal();
        }

        @Override
        public boolean tryLock() {
            return tryLocking();
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            return LockMimicSupport.tryLock(tryLocking(), time, unit, underlyingLock_waitHandle, underlyingLock);
        }

        @Override
        public void unlock() {
            ownerLock.lock();

            try {
                AbstractLockRing<TKey, TLock> fixedOwner = owner.get();

                fixedOwner.unlockedLocks.put(key, this);
                fixedOwner.unlockedLocks_waitHandle.countUp();
                underlyingLock.unlock();
            } finally {
                ownerLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return underlyingLock.newCondition();
        }
    }
}
