package com.dipasquale.threading;

import com.dipasquale.common.test.ThrowableAsserter;
import com.dipasquale.concurrent.ConcurrentId;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class ComparableLockTest {
    private static final AtomicInteger LOCKS_ACQUIRED = new AtomicInteger();
    private static final AtomicInteger LOCKS_RELEASED = new AtomicInteger();
    private static final Lock LOCK = createLock();

    private static Lock createLock() {
        return new Lock() {
            @Override
            public void lock() {
                LOCKS_ACQUIRED.incrementAndGet();
            }

            @Override
            public void lockInterruptibly()
                    throws InterruptedException {
                LOCKS_ACQUIRED.incrementAndGet();
            }

            @Override
            public boolean tryLock() {
                return LOCKS_ACQUIRED.getAndIncrement() == 0;
            }

            @Override
            public boolean tryLock(final long time, final TimeUnit unit)
                    throws InterruptedException {
                LOCKS_ACQUIRED.incrementAndGet();

                return time > 0L && unit != null;
            }

            @Override
            public void unlock() {
                LOCKS_RELEASED.incrementAndGet();
            }

            @Override
            public Condition newCondition() {
                throw new IllegalStateException("no need to create a new condition");
            }
        };
    }

    @Test
    public void TEST_1() {
        ComparableLock test = ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 0));

        Assert.assertEquals(LOCK, test.getUnitTest().getLock());
    }

    @Test
    public void TEST_2() {
        Assert.assertEquals(0, ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 0))));

        Assert.assertEquals(0, ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(-1, ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(-1, ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(0, ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(-1, ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))));

        Assert.assertEquals(1, ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1))));

        Assert.assertEquals(1, ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(0, 1, 0))));

        Assert.assertEquals(0, ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))
                .compareTo(ComparableLock.create(LOCK, new ConcurrentId<>(1, 0, 0))));
    }

    @Test
    public void TEST_3() {
        ComparableLock test = ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1));

        Assert.assertEquals("0.0.1", test.toString());
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        ComparableLock test = ComparableLock.create(LOCK, new ConcurrentId<>(0, 0, 1));

        test.lock();
        Assert.assertEquals(1, LOCKS_ACQUIRED.get());
        test.lockInterruptibly();
        Assert.assertEquals(2, LOCKS_ACQUIRED.get());
        Assert.assertFalse(test.tryLock());
        Assert.assertEquals(3, LOCKS_ACQUIRED.get());
        Assert.assertFalse(test.tryLock(0L, null));
        Assert.assertEquals(4, LOCKS_ACQUIRED.get());
        Assert.assertFalse(test.tryLock(1L, null));
        Assert.assertEquals(5, LOCKS_ACQUIRED.get());
        Assert.assertTrue(test.tryLock(1L, TimeUnit.HOURS));
        Assert.assertEquals(6, LOCKS_ACQUIRED.get());
        test.unlock();
        Assert.assertEquals(1, LOCKS_RELEASED.get());

        try {
            test.newCondition(); // TODO: redo this
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalStateException.class)
                    .message("no need to create a new condition")
                    .build(), ThrowableAsserter.create(e));
        }

        LOCKS_ACQUIRED.set(0);
        Assert.assertTrue(test.tryLock());
    }
}
