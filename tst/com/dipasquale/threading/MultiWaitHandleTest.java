package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.test.ThrowableAsserter;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
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
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.create(DATE_TIME_SUPPORT, a -> false, waitHandles, WaitHandle::await, WaitHandle::await);

        test.await();

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_2()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        test.await();

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .indefinite(1)
                        .build())
                .add(WaitHandle.builder()
                        .indefinite(1)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_3()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        try {
            test.await();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .indefinite(0)
                        .build())
                .add(WaitHandle.builder()
                        .indefinite(0)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        try {
            test.await();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .indefinite(1)
                        .build())
                .add(WaitHandle.builder()
                        .indefinite(0)
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_5()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.create(DATE_TIME_SUPPORT, a -> false, waitHandles, WaitHandle::await, WaitHandle::await);

        Assert.assertTrue(test.await(10L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_6()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        Assert.assertTrue(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(4L, TimeUnit.MILLISECONDS))
                                .add(new Timed(2L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(3L, TimeUnit.MILLISECONDS))
                                .add(new Timed(1L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(5L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_7() {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_8() {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(2L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_9()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .acquired(false)
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        Assert.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandle.builder()
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_10()
            throws InterruptedException {
        ImmutableList<WaitHandle> waitHandles = ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .build())
                .add(WaitHandle.builder()
                        .acquired(false)
                        .build())
                .build();

        MultiWaitHandle test = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, waitHandles, WaitHandle::await, WaitHandle::await);

        Assert.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assert.assertEquals(ImmutableList.<WaitHandle>builder()
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(4L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .add(WaitHandle.builder()
                        .timed(ImmutableList.<Timed>builder()
                                .add(new Timed(3L, TimeUnit.MILLISECONDS))
                                .build())
                        .build())
                .build(), waitHandles);

        Assert.assertEquals(2L, CURRENT_DATE_TIME.get());
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @ToString
    private static final class Timed {
        private final long timeout;
        private final TimeUnit unit;
    }

    @Builder
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString(onlyExplicitlyIncluded = true)
    private static final class WaitHandle {
        @Builder.Default
        @EqualsAndHashCode.Include
        @ToString.Include
        private int indefinite = 0;
        @Builder.Default
        @EqualsAndHashCode.Include
        @ToString.Include
        private final List<Timed> timed = new ArrayList<>();
        @Builder.Default
        private final boolean acquired = true;
        @Builder.Default
        private final String interruptedExceptionMessage = null;

        public void await()
                throws InterruptedException {
            if (interruptedExceptionMessage != null) {
                throw new InterruptedException(interruptedExceptionMessage);
            }

            indefinite++;
        }

        public boolean await(final long timeout, final TimeUnit unit)
                throws InterruptedException {
            if (interruptedExceptionMessage != null) {
                throw new InterruptedException(interruptedExceptionMessage);
            }

            timed.add(new Timed(timeout, unit));

            return acquired;
        }
    }
}
