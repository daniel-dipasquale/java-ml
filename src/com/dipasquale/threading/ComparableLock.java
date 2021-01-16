package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionHandler;
import com.dipasquale.concurrent.ConcurrentId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComparableLock implements Comparable<ComparableLock>, Lock {
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.createNanoseconds();
    private final Lock lock;
    private final Comparable<Object> concurrentId;
    @Getter(AccessLevel.PACKAGE)
    private final UnitTest unitTest = new UnitTest();

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    public static <T extends Comparable<T>> ComparableLock create(final Lock lock, final ConcurrentId<T> concurrentId) {
        return new ComparableLock(lock, ensureType(concurrentId));
    }

    public static Lock createAggregated(final Iterable<ComparableLock> locks) {
        List<ComparableLock> sortedLocks = StreamSupport.stream(locks.spliterator(), false)
                .sorted()
                .collect(Collectors.toList());

        return new MultiLock(sortedLocks);
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly()
            throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit)
            throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public int compareTo(final ComparableLock comparableLock) {
        return concurrentId.compareTo(comparableLock.concurrentId);
    }

    @Override
    public String toString() {
        return concurrentId.toString();
    }

    final class UnitTest {
        public Lock getLock() {
            return lock;
        }
    }

    private static final class MultiCondition implements Condition {
        private final UnitConverter FROM_MS_TO_NS_UNIT_CONVERTER = SI.MILLI(SI.SECOND).getConverterTo(DATE_TIME_SUPPORT.unit());
        private final TimeUnit NS_TIME_UNIT = DATE_TIME_SUPPORT.timeUnit();
        private final List<Condition> conditions;
        private final MultiWaitHandle conditionsWaitHandle;
        private final ExceptionHandler conditionsAwaitExceptionHandler;

        MultiCondition(final List<Condition> conditions) {
            this.conditions = conditions;
            this.conditionsWaitHandle = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, conditions, null, Condition::await);
            this.conditionsAwaitExceptionHandler = ExceptionHandler.create(conditions, Condition::await);
        }

        @Override
        public void await()
                throws InterruptedException {
            conditionsAwaitExceptionHandler.invokeAllAndThrowAsSuppressedIfAny(() -> new InterruptedException("unable to await on all conditions"));
        }

        @Override
        public void awaitUninterruptibly() {
            conditions.forEach(Condition::awaitUninterruptibly);
        }

        @Override
        public long awaitNanos(final long nanosTimeout)
                throws InterruptedException {
            long startDateTime = DATE_TIME_SUPPORT.now();

            conditionsWaitHandle.await(nanosTimeout, NS_TIME_UNIT);

            return nanosTimeout - DATE_TIME_SUPPORT.now() + startDateTime;
        }

        @Override
        public boolean await(final long time, final TimeUnit unit)
                throws InterruptedException {
            return conditionsWaitHandle.await(time, unit);
        }

        @Override
        public boolean awaitUntil(final Date deadline)
                throws InterruptedException {
            long deadlineDateTime = (long) FROM_MS_TO_NS_UNIT_CONVERTER.convert((double) deadline.getTime());
            long currentDateTime = DATE_TIME_SUPPORT.now();
            long time = Math.max(deadlineDateTime - currentDateTime, 0L);

            return conditionsWaitHandle.await(time, NS_TIME_UNIT);
        }

        @Override
        public void signal() {
            conditions.forEach(Condition::signal);
        }

        @Override
        public void signalAll() {
            conditions.forEach(Condition::signalAll);
        }
    }

    private static final class MultiLock implements Lock {
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
}
