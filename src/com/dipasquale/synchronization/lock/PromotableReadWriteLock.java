package com.dipasquale.synchronization.lock;

import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.ReusableCountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitCondition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class PromotableReadWriteLock implements ReadWriteLock, Serializable {
    @Serial
    private static final long serialVersionUID = 1309636016239332703L;
    private final ReadLock readLock;
    private final WriteLock writeLock;

    private PromotableReadWriteLock(final ReentrantReadWriteLock readWriteLock, final Lock promoteLock) {
        this.readLock = new ReadLock(readWriteLock);
        this.writeLock = new WriteLock(promoteLock, readWriteLock);
    }

    private PromotableReadWriteLock(final ReentrantReadWriteLock readWriteLock) {
        this(readWriteLock, new ReentrantLock());
    }

    public PromotableReadWriteLock(final boolean fair) {
        this(new ReentrantReadWriteLock(fair));
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    private static final class ReadLock implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = 2373320992415917573L;
        private final ReentrantReadWriteLock readWriteLock;
        private final Lock underlyingLock;

        private ReadLock(final ReentrantReadWriteLock readWriteLock) {
            this.readWriteLock = readWriteLock;
            this.underlyingLock = readWriteLock.readLock();
        }

        @Override
        public void lock() {
            if (!readWriteLock.isWriteLockedByCurrentThread()) {
                underlyingLock.lock();
            }
        }

        @Override
        public void lockInterruptibly()
                throws InterruptedException {
            if (!readWriteLock.isWriteLockedByCurrentThread()) {
                underlyingLock.lockInterruptibly();
            }
        }

        @Override
        public boolean tryLock() {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                return true;
            }

            return underlyingLock.tryLock();
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                return true;
            }

            return underlyingLock.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            if (!readWriteLock.isWriteLockedByCurrentThread()) { // TODO: consider keeping track of the number of locks to build an illegalMonitorException condition if the count does not match
                underlyingLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return underlyingLock.newCondition();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class WriteLockContext implements Serializable {
        @Serial
        private static final long serialVersionUID = -6579970677300885175L;
        private int readHoldCount = 0;
    }

    private static final class WriteLock implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = 3279758705121405761L;
        private final Lock promoteLock;
        private final ReusableCountDownWaitHandle promoteLock_waitHandle;
        private final ReentrantReadWriteLock readWriteLock;
        private final ReentrantReadWriteLock.ReadLock readLock;
        private transient ThreadLocal<WriteLockContext> context;
        private final Lock underlyingLock;
        private final ReusableCountDownWaitHandle underlyingLock_waitHandle;

        private WriteLock(final Lock promoteLock, final ReentrantReadWriteLock readWriteLock) {
            this.promoteLock = promoteLock;
            this.promoteLock_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_NOT_ZERO);
            this.readWriteLock = readWriteLock;
            this.readLock = readWriteLock.readLock();
            this.context = ThreadLocal.withInitial(WriteLockContext::new);
            this.underlyingLock = readWriteLock.writeLock();
            this.underlyingLock_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_NOT_ZERO);
        }

        @Serial
        private void readObject(final ObjectInputStream objectInputStream)
                throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            context = ThreadLocal.withInitial(WriteLockContext::new);
        }

        private void unlockReadIfHeld() {
            int holdCount = readWriteLock.getReadHoldCount();

            if (holdCount > 0) {
                for (int i = 0; i < holdCount; i++) {
                    readLock.unlock();
                }
            }

            context.get().readHoldCount = holdCount;
        }

        private boolean tryLocking(final boolean forced) {
            boolean locked = promoteLock.tryLock();

            try {
                if (!locked) {
                    if (!forced) {
                        return false;
                    }

                    unlockReadIfHeld(); // NOTE: this means that in order to avoid a deadlock when promoting, the state of the write-lock once cleared is not guaranteed to be the same as it was when it was read for the waiting threads

                    do {
                        try {
                            promoteLock_waitHandle.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        locked = promoteLock.tryLock();
                    } while (!locked);
                } else {
                    unlockReadIfHeld();
                }

                promoteLock_waitHandle.countUp();

                return underlyingLock.tryLock();
            } finally {
                if (locked) {
                    LockMimicSupport.unlock(promoteLock_waitHandle, promoteLock);
                }
            }
        }

        private void lockInternal()
                throws InterruptedException {
            boolean locked = tryLocking(true);

            LockMimicSupport.lock(locked, underlyingLock_waitHandle, underlyingLock, promoteLock_waitHandle);
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
            boolean locked = tryLocking(false);

            return LockMimicSupport.tryLock(locked, underlyingLock_waitHandle);
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            boolean locked = tryLocking(false);

            return LockMimicSupport.tryLock(locked, time, unit, underlyingLock_waitHandle, underlyingLock, promoteLock_waitHandle);
        }

        @Override
        public void unlock() {
            promoteLock.lock();
            promoteLock_waitHandle.countUp();

            try {
                int readHoldCount = context.get().readHoldCount;

                LockMimicSupport.unlock(underlyingLock_waitHandle, underlyingLock);

                for (int i = 0; i < readHoldCount; i++) {
                    boolean locked = readLock.tryLock();

                    assert locked;
                }

                context.remove();
            } finally {
                LockMimicSupport.unlock(promoteLock_waitHandle, promoteLock);
            }
        }

        @Override
        public Condition newCondition() {
            return underlyingLock.newCondition();
        }
    }
}
