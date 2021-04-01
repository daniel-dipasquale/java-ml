package com.dipasquale.threading;

import com.dipasquale.common.MultiExceptionHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class MultiLock implements Lock {
    private final List<ComparableLock> locks;
    private final MultiWaitHandle waitWhileTryLockAllLocksHandle;
    private final MultiExceptionHandler lockInterruptiblyAllLocksHandler;
    private final Object sync;

    public static Lock create(final Iterable<ComparableLock> locks) {
        List<ComparableLock> sortedLocks = StreamSupport.stream(locks.spliterator(), false)
                .sorted()
                .collect(Collectors.toList());

        MultiWaitHandle locksTryLock = MultiWaitHandle.createSinglePass(ThreadingConstants.DATE_TIME_SUPPORT_NANOSECONDS, sortedLocks, null, Lock::tryLock);
        MultiExceptionHandler locksLockInterruptiblyExceptionHandler = MultiExceptionHandler.create(sortedLocks, ComparableLock::lockInterruptibly);
        Object sync = new Object();

        return new MultiLock(sortedLocks, locksTryLock, locksLockInterruptiblyExceptionHandler, sync);
    }

    @Override
    public void lock() {
        synchronized (sync) {
            locks.forEach(ComparableLock::lock);
        }
    }

    @Override
    public void lockInterruptibly()
            throws InterruptedException {
        synchronized (sync) {
            lockInterruptiblyAllLocksHandler.invokeAllAndThrowAsSuppressedIfAny(() -> new InterruptedException("unable to lock interruptibly on all locks"));
        }
    }

    @Override
    public boolean tryLock() {
        synchronized (sync) {
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
        synchronized (sync) {
            return waitWhileTryLockAllLocksHandle.await(time, unit);
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
