package com.dipasquale.threading.lock;

import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

final class MultiLock<T extends Comparable<T>> implements Lock {
    private final List<ComparableLock<T>> locks;
    private final IterableErrorHandler<ComparableLock<T>> locksLockInterruptiblyHandler;
    private final MultiWaitHandle locksWaitHandleUntilTimeout;

    MultiLock(final Iterable<ComparableLock<T>> locks) {
        List<ComparableLock<T>> locksFixed = StreamSupport.stream(locks.spliterator(), false)
                .sorted()
                .collect(Collectors.toList());

        this.locks = locksFixed;
        this.locksLockInterruptiblyHandler = new IterableErrorHandler<>(locksFixed, ComparableLock::lockInterruptibly);
        this.locksWaitHandleUntilTimeout = MultiWaitHandle.create(locksFixed, LockWaitHandle::new, Constants.DATE_TIME_SUPPORT_NANOSECONDS);
    }

    @Override
    public void lock() {
        synchronized (locks) {
            locks.forEach(ComparableLock::lock);
        }
    }

    @Override
    public void lockInterruptibly()
            throws InterruptedException {
        synchronized (locks) {
            locksLockInterruptiblyHandler.handleAll(() -> new InterruptedException("unable to lock interruptibly on all locks"));
        }
    }

    @Override
    public boolean tryLock() {
        synchronized (locks) {
            List<Lock> locked = new ArrayList<>();

            for (Lock lock : locks) {
                if (lock.tryLock()) {
                    locked.add(lock);
                }
            }

            if (locked.size() == locks.size()) {
                return true;
            }

            for (int i = locked.size() - 1; i >= 0; i--) {
                locked.get(i).unlock();
            }

            return false;
        }
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit)
            throws InterruptedException {
        synchronized (locks) {
            return locksWaitHandleUntilTimeout.await(time, unit);
        }
    }

    @Override
    public void unlock() {
        for (int i = locks.size() - 1; i >= 0; i--) {
            locks.get(i).unlock();
        }
    }

    @Override
    public Condition newCondition() {
        List<Condition> conditions = locks.stream()
                .map(ComparableLock::newCondition)
                .collect(Collectors.toList());

        return new MultiCondition(conditions);
    }
}
