package com.dipasquale.threading;

import com.dipasquale.common.test.ThrowableAsserter;
import com.dipasquale.concurrent.ConcurrentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Assert;
import org.junit.Test;

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
        ComparableLock test = ComparableLock.create(lock, new ConcurrentId<>(0, 0, 0));

        Assert.assertEquals(lock, test.getUnitTest().getLock());
    }

    @Test
    public void TEST_2() {
        Lock lock = createLock();

        Assert.assertEquals(0, ComparableLock.create(lock, new ConcurrentId<>(0, 0, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 0, 0))));

        Assert.assertEquals(0, ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(-1, ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(-1, ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(0, ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(-1, ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(1, ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(0, ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(lock, new ConcurrentId<>(1, 0, 0))));
    }

    @Test
    public void TEST_3() {
        Lock lock = createLock();
        ComparableLock test = ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1));

        Assert.assertEquals("0.0.1", test.toString());
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        LockState lockState = LockState.builder()
                .build();

        Lock lock = createLock(lockState);
        ComparableLock test = ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1));

        test.lock();
        Assert.assertEquals(1, lockState.acquired);
        test.lockInterruptibly();
        Assert.assertEquals(2, lockState.acquired);
        Assert.assertFalse(test.tryLock());
        Assert.assertEquals(3, lockState.acquired);
        Assert.assertFalse(test.tryLock(0L, null));
        Assert.assertEquals(4, lockState.acquired);
        Assert.assertFalse(test.tryLock(1L, null));
        Assert.assertEquals(5, lockState.acquired);
        Assert.assertTrue(test.tryLock(1L, TimeUnit.HOURS));
        Assert.assertEquals(6, lockState.acquired);
        test.unlock();
        Assert.assertEquals(1, lockState.released);
        lockState.acquired = 0;
        Assert.assertTrue(test.tryLock());

        Assert.assertEquals(LockState.builder()
                .acquired(1)
                .released(1)
                .build(), lockState);
    }

    @Test
    public void TEST_5()
            throws InterruptedException {
        LockState lockState = LockState.builder()
                .interruptedExceptionMessage("this is an interruption for unit test case purposes")
                .build();

        Lock lock = createLock(lockState);
        ComparableLock test = ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1));

        try {
            test.lockInterruptibly();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(0L, null);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(1L, null);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(0, lockState.acquired);

        try {
            test.tryLock(1L, TimeUnit.HOURS);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("this is an interruption for unit test case purposes")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(0, lockState.acquired);

        Assert.assertEquals(LockState.builder()
                .interruptedExceptionMessage("this is an interruption for unit test case purposes")
                .build(), lockState);
    }

//    @Test
//    public void TEST_6()
//            throws InterruptedException {
//        LockState lockState = LockState.builder()
//                .build();
//
//        Lock lock = createLock(lockState);
//        ComparableLock comparableLock = ComparableLock.create(lock, new ConcurrentId<>(0, 0, 1));
//        Condition test = comparableLock.newCondition();
//    }

    @Builder
    @EqualsAndHashCode
    @ToString
    private static final class ConditionState {
        private int acquired;
        private int released;
        private int signal;
        private int signalAll;
    }

    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static final class LockState {
        private int acquired;
        private int released;
        @Builder.Default
        private final List<ConditionState> conditions = new ArrayList<>();
        private final String interruptedExceptionMessage;
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

            return new Condition() {
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

                    return nanosTimeout;
                }

                @Override
                public boolean await(final long time, final TimeUnit unit)
                        throws InterruptedException {
                    if (lockState.interruptedExceptionMessage != null) {
                        throw new InterruptedException(lockState.interruptedExceptionMessage);
                    }

                    return false;
                }

                @Override
                public boolean awaitUntil(final Date deadline)
                        throws InterruptedException {
                    if (lockState.interruptedExceptionMessage != null) {
                        throw new InterruptedException(lockState.interruptedExceptionMessage);
                    }

                    return false;
                }

                @Override
                public void signal() {
                    conditionState.signal++;
                }

                @Override
                public void signalAll() {
                    conditionState.signalAll++;
                }
            };
        }
    }
}
