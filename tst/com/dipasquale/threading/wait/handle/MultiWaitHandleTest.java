package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.test.ThrowableComparer;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class MultiWaitHandleTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.create(CURRENT_DATE_TIME::incrementAndGet, SI.MILLI(SI.SECOND));

    @Before
    public void before() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void TEST_1()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, a -> false, waitHandles);

        test.await();

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_2()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        test.await();

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .indefinite(1)
                        .build())
                .add(WaitHandleMock.builder()
                        .indefinite(1)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_3()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        try {
            test.await();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .indefinite(0)
                        .build())
                .add(WaitHandleMock.builder()
                        .indefinite(0)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        try {
            test.await();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .indefinite(1)
                        .build())
                .add(WaitHandleMock.builder()
                        .indefinite(0)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_5()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, a -> false, waitHandles);

        Assert.assertTrue(test.await(10L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_6()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        Assert.assertTrue(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(3L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(3L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_7()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, a -> true, waitHandles);

        Assert.assertTrue(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(4L, TimeUnit.MILLISECONDS))
                                .add(new TimeUnitPair(2L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(3L, TimeUnit.MILLISECONDS))
                                .add(new TimeUnitPair(1L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(5L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_8() {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_9() {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ThrowableComparer.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(2L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_10()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .acquired(false)
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        Assert.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandleMock.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_11()
            throws InterruptedException {
        ImmutableList<WaitHandleMock> waitHandles = ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .build())
                .add(WaitHandleMock.builder()
                        .acquired(false)
                        .build())
                .build();

        MultiWaitHandle test = new MultiWaitHandle(DATE_TIME_SUPPORT, waitHandles);

        Assert.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandleMock>builder()
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandleMock.builder()
                        .timed(ImmutableList.<TimeUnitPair>builder()
                                .add(new TimeUnitPair(3L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(2L, CURRENT_DATE_TIME.get());
    }

    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString(onlyExplicitlyIncluded = true)
    private static final class WaitHandleMock implements WaitHandle {
        @Builder.Default
        @EqualsAndHashCode.Include
        @ToString.Include
        private int indefinite = 0;
        @Builder.Default
        @EqualsAndHashCode.Include
        @ToString.Include
        private final List<TimeUnitPair> timed = new ArrayList<>();
        @Builder.Default
        private final boolean acquired = true;
        @Builder.Default
        private final String interruptedExceptionMessage = null;

        @Override
        public void await()
                throws InterruptedException {
            if (interruptedExceptionMessage != null) {
                throw new InterruptedException(interruptedExceptionMessage);
            }

            indefinite++;
        }

        @Override
        public boolean await(final long timeout, final TimeUnit unit)
                throws InterruptedException {
            if (interruptedExceptionMessage != null) {
                throw new InterruptedException(interruptedExceptionMessage);
            }

            timed.add(new TimeUnitPair(timeout, unit));

            return acquired;
        }
    }
}
