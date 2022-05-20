package com.dipasquale.synchronization.lock;

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
abstract class AbstractLockRing<TKey extends Comparable<TKey>, TLock> implements Serializable {
    @Serial
    private static final long serialVersionUID = 6679248328057781835L;
    private final NavigableMap<TKey, RegisteredLock<TKey, TLock>> unlockedLocks = new ConcurrentSkipListMap<>(Comparator.naturalOrder()); // TODO: consider randomizing the comparator, to avoid biased selection
    private final ReusableCountDownWaitHandle unlockedLocks_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_ZERO);

    protected RegisteredLock<TKey, TLock> createLock(final TKey key, final Lock underlyingLock, final TLock actualLock) {
        RegisteredLock<TKey, TLock> registeredLock = new RegisteredLock<>(key, underlyingLock, actualLock, this);

        unlockedLocks.put(key, registeredLock);
        unlockedLocks_waitHandle.countUp();

        return registeredLock;
    }

    private TLock awaitAnyUnlocked(final AwaitHandler awaitHandler)
            throws InterruptedException {
        while (true) {
            awaitHandler.invoke();

            Map.Entry<TKey, RegisteredLock<TKey, TLock>> lockEntry = unlockedLocks.firstEntry();

            if (lockEntry != null) {
                return lockEntry.getValue().actualLock;
            }
        }
    }

    public TLock awaitAnyUnlocked()
            throws InterruptedException {
        return awaitAnyUnlocked(unlockedLocks_waitHandle::await);
    }

    public TLock awaitAnyUnlocked(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return awaitAnyUnlocked(() -> unlockedLocks_waitHandle.await(timeout, unit));
    }

    private static <TKey extends Comparable<TKey>, TLock> Boolean lockInternal(final RegisteredLock<TKey, TLock> registeredLock, final UnderlyingLockHandler underlyingLockHandler)
            throws InterruptedException {
        registeredLock.wrapperLock.lock();

        AbstractLockRing<TKey, TLock> lockRingOwner = registeredLock.lockRingOwner.get();

        try {
            lockRingOwner.unlockedLocks_waitHandle.countDown();
            lockRingOwner.unlockedLocks.remove(registeredLock.key);

            return underlyingLockHandler.invoke();
        } finally {
            lockRingOwner.unlockedLocks.put(registeredLock.key, registeredLock);
            lockRingOwner.unlockedLocks_waitHandle.countUp();
            registeredLock.wrapperLock.unlock();
        }
    }

    @FunctionalInterface
    private interface AwaitHandler {
        void invoke() throws InterruptedException;
    }

    @FunctionalInterface
    private interface UnderlyingLockHandler {
        Boolean invoke() throws InterruptedException;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected static final class RegisteredLock<TKey extends Comparable<TKey>, TLock> implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = -8154738479719064226L;
        @Getter
        private final TKey key;
        private final Lock wrapperLock;
        private final Lock underlyingLock;
        private final TLock actualLock;
        private final WeakReference<AbstractLockRing<TKey, TLock>> lockRingOwner;

        private RegisteredLock(final TKey key, final Lock underlyingLock, final TLock actualLock, final AbstractLockRing<TKey, TLock> lockRingOwner) {
            this.key = key;
            this.wrapperLock = new ReentrantLock();
            this.underlyingLock = underlyingLock;
            this.actualLock = actualLock;
            this.lockRingOwner = new WeakReference<>(lockRingOwner);
        }

        @Override
        public void lock() {
            try {
                lockInternal(this, () -> {
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
            lockInternal(this, () -> {
                underlyingLock.lockInterruptibly();

                return null;
            });
        }

        @Override
        public boolean tryLock() {
            try {
                return lockInternal(this, underlyingLock::tryLock);
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException("underlying lock was interrupted", e);
            }
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            return lockInternal(this, () -> underlyingLock.tryLock(time, unit));
        }

        @Override
        public void unlock() {
            underlyingLock.unlock();
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }
}
