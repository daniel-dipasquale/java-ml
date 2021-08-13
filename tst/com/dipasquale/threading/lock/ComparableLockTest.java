/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.lock;

import com.dipasquale.common.concurrent.ConcurrentId;
import com.dipasquale.common.error.ErrorComparer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class ComparableLockTest {
    private static Lock createLock(final LockState lockState) {
        return new LockMock(lockState);
    }

    private static Lock createLock() {
        LockState lockState = LockState.builder()
                .build();

        return createLock(lockState);
    }

    @Test
    public void TEST_1() {
        Lock lock = createLock();
        ComparableLock<Integer> test000 = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 0));
        ComparableLock<Integer> test001 = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));
        ComparableLock<Integer> test010 = new ComparableLock<>(lock, new ConcurrentId<>(0, 1, 0));
        ComparableLock<Integer> test100 = new ComparableLock<>(lock, new ConcurrentId<>(1, 0, 0));

        Assertions.assertEquals(0, test000.compareTo(test000));
        Assertions.assertEquals(0, test001.compareTo(test001));
        Assertions.assertEquals(-1, test001.compareTo(test010));
        Assertions.assertEquals(-1, test001.compareTo(test100));
        Assertions.assertEquals(1, test010.compareTo(test000));
        Assertions.assertEquals(1, test010.compareTo(test001));
        Assertions.assertEquals(0, test010.compareTo(test010));
        Assertions.assertEquals(-1, test010.compareTo(test100));
        Assertions.assertEquals(1, test100.compareTo(test001));
        Assertions.assertEquals(1, test100.compareTo(test010));
        Assertions.assertEquals(0, test100.compareTo(test100));
    }

    @Test
    public void TEST_2() {
        Lock lock = createLock();
        ComparableLock<Integer> test = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));

        Assertions.assertEquals("0.0.1", test.toString());
    }

    @Test
    public void TEST_3()
            throws InterruptedException {
        LockState lockState = LockState.builder()
                .build();

        Lock lock = createLock(lockState);
        ComparableLock<Integer> test = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));

        test.lock();
        Assertions.assertEquals(1, lockState.acquired);
        test.lockInterruptibly();
        Assertions.assertEquals(2, lockState.acquired);
        Assertions.assertFalse(test.tryLock());
        Assertions.assertEquals(3, lockState.acquired);
        Assertions.assertFalse(test.tryLock(0L, null));
        Assertions.assertEquals(4, lockState.acquired);
        Assertions.assertFalse(test.tryLock(1L, null));
        Assertions.assertEquals(5, lockState.acquired);
        Assertions.assertTrue(test.tryLock(1L, TimeUnit.HOURS));
        Assertions.assertEquals(6, lockState.acquired);
        test.unlock();
        Assertions.assertEquals(1, lockState.released);
        lockState.acquired = 0;
        Assertions.assertTrue(test.tryLock());

        Assertions.assertEquals(LockState.builder()
                .acquired(1)
                .released(1)
                .build(), lockState);
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        LockState lockState = LockState.builder()
                .interruptedExceptionMessage("this is an interruption for unit test case purposes")
                .build();

        Lock lock = createLock(lockState);
        ComparableLock<Integer> test = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));

        try {
            test.lockInterruptibly();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(0L, null);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(1L, null);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(1L, TimeUnit.HOURS);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.acquired);

        Assertions.assertEquals(LockState.builder()
                .interruptedExceptionMessage("this is an interruption for unit test case purposes")
                .build(), lockState);
    }

    @Test
    public void TEST_5()
            throws InterruptedException {
        LockState lockState = LockState.builder()
                .build();

        Lock lock = createLock(lockState);
        ComparableLock<Integer> comparableLock = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));
        Condition test = comparableLock.newCondition();

        test.await();
        Assertions.assertEquals(1, lockState.conditions.get(0).acquired);
        test.awaitUninterruptibly();
        Assertions.assertEquals(2, lockState.conditions.get(0).acquired);
        Assertions.assertEquals(10L, test.awaitNanos(10L));
        Assertions.assertEquals(3, lockState.conditions.get(0).acquired);
        Assertions.assertFalse(test.await(0L, null));
        Assertions.assertEquals(4, lockState.conditions.get(0).acquired);
        Assertions.assertFalse(test.await(1L, null));
        Assertions.assertEquals(5, lockState.conditions.get(0).acquired);
        Assertions.assertTrue(test.await(1L, TimeUnit.HOURS));
        Assertions.assertEquals(6, lockState.conditions.get(0).acquired);
        Assertions.assertFalse(test.awaitUntil(new Date(0L)));
        Assertions.assertEquals(7, lockState.conditions.get(0).acquired);
        Assertions.assertTrue(test.awaitUntil(new Date(1L)));
        Assertions.assertEquals(8, lockState.conditions.get(0).acquired);
        test.signal();
        Assertions.assertEquals(1, lockState.conditions.get(0).signal);
        test.signalAll();
        Assertions.assertEquals(1, lockState.conditions.get(0).signalAll);
    }

    @Test
    public void TEST_7() {
        LockState lockState = LockState.builder()
                .interruptedExceptionMessage("this is an interruption for unit test case purposes")
                .build();

        Lock lock = createLock(lockState);
        ComparableLock<Integer> comparableLock = new ComparableLock<>(lock, new ConcurrentId<>(0, 0, 1));
        Condition test = comparableLock.newCondition();

        try {
            test.await();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);

        try {
            test.awaitNanos(10L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);

        try {
            test.await(0L, null);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);

        try {
            test.await(1L, null);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);

        try {
            test.await(1L, TimeUnit.HOURS);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);

        try {
            test.awaitUntil(null);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ErrorComparer.create(e));
        }

        Assertions.assertEquals(0, lockState.conditions.get(0).acquired);
    }

    @Builder(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @ToString
    private static final class ConditionState {
        private int acquired;
        private int signal;
        private int signalAll;
    }

    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @ToString
    private static final class LockState {
        private int acquired;
        private int released;
        @Builder.Default
        private final List<ConditionState> conditions = new ArrayList<>();
        private final String interruptedExceptionMessage;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ConditionMock implements Condition {
        private final LockState lockState;
        private final ConditionState conditionState;

        @Override
        public void await()
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            conditionState.acquired++;
        }

        @Override
        public void awaitUninterruptibly() {
            conditionState.acquired++;
        }

        @Override
        public long awaitNanos(final long nanosTimeout)
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            conditionState.acquired++;

            return nanosTimeout;
        }

        @Override
        public boolean await(final long time, final TimeUnit unit)
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            conditionState.acquired++;

            return time > 0L && unit != null;
        }

        @Override
        public boolean awaitUntil(final Date deadline)
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            conditionState.acquired++;

            return deadline.getTime() > 0L;
        }

        @Override
        public void signal() {
            conditionState.signal++;
        }

        @Override
        public void signalAll() {
            conditionState.signalAll++;
        }
    }

    @RequiredArgsConstructor
    private static final class LockMock implements Lock {
        private final LockState lockState;

        @Override
        public void lock() {
            lockState.acquired++;
        }

        @Override
        public void lockInterruptibly()
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            lockState.acquired++;
        }

        @Override
        public boolean tryLock() {
            return lockState.acquired++ == 0;
        }

        @Override
        public boolean tryLock(final long time, final TimeUnit unit)
                throws InterruptedException {
            if (lockState.interruptedExceptionMessage != null) {
                throw new InterruptedException(lockState.interruptedExceptionMessage);
            }

            lockState.acquired++;

            return time > 0L && unit != null;
        }

        @Override
        public void unlock() {
            lockState.released++;
        }

        @Override
        public Condition newCondition() {
            ConditionState conditionState = ConditionState.builder()
                    .build();

            lockState.conditions.add(conditionState);

            return new ConditionMock(lockState, conditionState);
        }
    }
}
