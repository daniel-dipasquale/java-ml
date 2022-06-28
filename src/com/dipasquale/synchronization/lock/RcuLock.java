package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.IsolatedThreadIndex;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public final class RcuLock implements ReadWriteLock, Serializable {
    @Serial
    private static final long serialVersionUID = 8290837675748248070L;
    private final RcuController controller;
    private final ReadLock readLock;
    private final WriteLock writeLock;

    private RcuLock(final RcuController controller) {
        this.controller = controller;
        this.readLock = new ReadLock(controller);
        this.writeLock = new WriteLock(controller);
    }

    public RcuLock(final IsolatedThreadIndex isolatedThreadIndex) {
        this(new IsolatedRcuController(isolatedThreadIndex));
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    public <T> RcuMonitoredReference<T> createMonitoredReference(final T reference, final ObjectCloner<T> referenceCloner) {
        return controller.createMonitoredReference(reference, referenceCloner);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReadLock implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = -210278138904629283L;
        private final RcuController controller;

        private void lockInternal() {
            controller.acquireRead();
        }

        @Override
        public void lock() {
            lockInternal();
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
            lockInternal();

            return true;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit) {
            lockInternal();

            return true;
        }

        @Override
        public void unlock() {
            controller.releaseRead();
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class WriteLock implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = -2696907262588806208L;
        private final ReentrantLock lock = new ReentrantLock();
        private final RcuController controller;

        private void lockInternal() {
            lock.lock();
            controller.acquireWrite();
        }

        @Override
        public void lock() {
            lockInternal();
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
            if (!lock.tryLock()) {
                return false;
            }

            controller.acquireWrite();

            return true;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            if (!lock.tryLock(time, unit)) {
                return false;
            }

            controller.acquireWrite();

            return true;
        }

        @Override
        public void unlock() {
            controller.releaseWrite();
            lock.unlock();
        }

        @Override
        public Condition newCondition() {
            return lock.newCondition();
        }
    }
}
