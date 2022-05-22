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
        private final Lock readLock;
        private final Lock writeLock;

        private ReadLock(final ReentrantReadWriteLock readWriteLock) {
            this.readWriteLock = readWriteLock;
            this.readLock = readWriteLock.readLock();
            this.writeLock = readWriteLock.writeLock();
        }

        @Override
        public void lock() {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                writeLock.lock();
            } else {
                readLock.lock();
            }
        }

        @Override
        public void lockInterruptibly()
                throws InterruptedException {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                writeLock.lockInterruptibly();
            } else {
                readLock.lockInterruptibly();
            }
        }

        @Override
        public boolean tryLock() {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                return writeLock.tryLock();
            }

            return readLock.tryLock();
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                return writeLock.tryLock(time, unit);
            }

            return readLock.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            if (readWriteLock.isWriteLockedByCurrentThread()) {
                writeLock.unlock();
            } else {
                readLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return readLock.newCondition();
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
        private final ReentrantReadWriteLock readWriteLock;
        private final ReentrantReadWriteLock.ReadLock readLock;
        private transient ThreadLocal<WriteLockContext> context;
        private final Lock underlyingLock;
        private final ReusableCountDownWaitHandle underlyingLock_waitHandle;

        private WriteLock(final Lock promoteLock, final ReentrantReadWriteLock readWriteLock) {
            this.promoteLock = promoteLock;
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

        private void unlockReadLockIfHeld() {
            int holdCount = readWriteLock.getReadHoldCount();

            if (holdCount > 0) {
                for (int i = 0; i < holdCount; i++) {
                    readLock.unlock();
                }
            }

            context.get().readHoldCount = holdCount;
        }

        private boolean tryLocking() {
            boolean locked = promoteLock.tryLock();

            try {
                unlockReadLockIfHeld();

                if (!locked) {
                    do {
                        Thread.onSpinWait();
                        locked = promoteLock.tryLock();
                    } while (!locked);
                }

                return underlyingLock.tryLock();
            } finally {
                if (locked) {
                    promoteLock.unlock();
                }
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
            return LockMimicSupport.tryLock(tryLocking(), underlyingLock_waitHandle);
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            return LockMimicSupport.tryLock(tryLocking(), time, unit, underlyingLock_waitHandle, underlyingLock);
        }

        @Override
        public void unlock() {
            promoteLock.lock();

            try {
                int readHoldCount = context.get().readHoldCount;

                underlyingLock_waitHandle.countDown();
                underlyingLock.unlock();

                for (int i = 0; i < readHoldCount; i++) {
                    readLock.tryLock();
                }

                context.remove();
            } finally {
                promoteLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return underlyingLock.newCondition();
        }
    }
}
