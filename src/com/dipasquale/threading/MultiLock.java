package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

final class MultiLock implements Lock {
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.createNanoseconds();
    private final List<ComparableLock> locks;
    private final MultiWaitHandle locksTryLock;
    private final ExceptionHandler locksLockInterruptiblyExceptionHandler;

    MultiLock(final List<ComparableLock> locks) {
        this.locks = locks;
        this.locksTryLock = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, locks, null, Lock::tryLock);
        this.locksLockInterruptiblyExceptionHandler = ExceptionHandler.create(locks, ComparableLock::lockInterruptibly);
    }

    @Override
    public void lock() {
        locks.forEach(ComparableLock::lock);
    }

    @Override
    public void lockInterruptibly()
            throws InterruptedException {
        locksLockInterruptiblyExceptionHandler.invokeAllAndThrowAsSuppressedIfAny(() -> new InterruptedException("unable to lock interruptibly on all locks"));
    }

    @Override
    public boolean tryLock() {
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

    @Override
    public boolean tryLock(final long time, final TimeUnit unit)
            throws InterruptedException {
        return locksTryLock.await(time, unit);
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
