package com.dipasquale.synchronization.lock;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.NanosecondsDateTimeSupport;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.ReusableCountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitCondition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class PromotableReadWriteLock implements ReadWriteLock, Serializable {
    @Serial
    private static final long serialVersionUID = 1309636016239332703L;
    private static final DateTimeSupport DATE_TIME_SUPPORT = new NanosecondsDateTimeSupport();
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final WriteLock writeLock;

    private PromotableReadWriteLock(final ReentrantReadWriteLock readWriteLock) {
        this.readLock = readWriteLock.readLock();
        this.writeLock = new WriteLock(readWriteLock);
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class WriteLockContext implements Serializable {
        @Serial
        private static final long serialVersionUID = -6579970677300885175L;
        private final Stack<Integer> readHoldCounts = new Stack<>();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class TimeTracking {
        private long remaining;
        private long offsetDateTime;

        private TimeTracking(final long time, final TimeUnit unit) {
            this.remaining = DATE_TIME_SUPPORT.timeUnit().convert(time, unit);
            this.offsetDateTime = DATE_TIME_SUPPORT.now();
        }

        private void update() {
            long currentDateTime = DATE_TIME_SUPPORT.now();

            remaining -= currentDateTime - offsetDateTime;
            offsetDateTime = currentDateTime;
        }
    }

    private static final class WriteLock implements Lock, Serializable {
        @Serial
        private static final long serialVersionUID = 3279758705121405761L;
        private final ReentrantLock promoteLock;
        private final ReusableCountDownWaitHandle promoteLock_waitHandle;
        private final ReentrantReadWriteLock readWriteLock;
        private final ReentrantReadWriteLock.ReadLock readLock;
        private transient ThreadLocal<WriteLockContext> context;
        private final ReentrantReadWriteLock.WriteLock underlyingLock;

        private WriteLock(final ReentrantReadWriteLock readWriteLock) {
            this.promoteLock = new ReentrantLock();
            this.promoteLock_waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_NOT_ZERO);
            this.readWriteLock = readWriteLock;
            this.readLock = readWriteLock.readLock();
            this.context = ThreadLocal.withInitial(WriteLockContext::new);
            this.underlyingLock = readWriteLock.writeLock();
        }

        @Serial
        private void readObject(final ObjectInputStream objectInputStream)
                throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            context = ThreadLocal.withInitial(WriteLockContext::new);
        }

        private void unlockReadIfHeld() {
            int holdCount = readWriteLock.getReadHoldCount();

            for (int i = 0; i < holdCount; i++) {
                readLock.unlock();
            }

            context.get().readHoldCounts.push(holdCount);
        }

        private boolean tryLocking(final boolean forcedToLock) {
            boolean locked = promoteLock.tryLock();

            if (!locked) {
                if (!forcedToLock) {
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

            return true;
        }

        private void lockInternal()
                throws InterruptedException {
            tryLocking(true);
            underlyingLock.lock();
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
            return tryLocking(false) && underlyingLock.tryLock();
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            boolean locked = tryLocking(false);

            if (!locked) {
                TimeTracking timeTracking = new TimeTracking(time, unit);

                do {
                    if (!promoteLock_waitHandle.await(timeTracking.remaining, DATE_TIME_SUPPORT.timeUnit())) {
                        return false;
                    }

                    timeTracking.update();
                    locked = tryLocking(false);
                } while (!locked);
            }

            return underlyingLock.tryLock();
        }

        @Override
        public void unlock() {
            assert promoteLock.isHeldByCurrentThread();

            try {
                Stack<Integer> readHoldCounts = context.get().readHoldCounts;
                int size = readHoldCounts.size();

                underlyingLock.unlock();

                if (size > 0) {
                    for (int i = 0, c = readHoldCounts.pop(); i < c; i++) {
                        boolean locked = readLock.tryLock();

                        assert locked;
                    }
                }

                if (size == 0) {
                    context.remove();
                }
            } finally {
                promoteLock_waitHandle.countDown();
                promoteLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return underlyingLock.newCondition();
        }
    }
}
