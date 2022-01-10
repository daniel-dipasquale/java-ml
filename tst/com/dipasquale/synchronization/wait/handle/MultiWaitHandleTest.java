package com.dipasquale.synchronization.wait.handle;

import com.dipasquale.common.error.ErrorComparator;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.ProxyDateTimeSupport;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class MultiWaitHandleTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport DATE_TIME_SUPPORT = new ProxyDateTimeSupport(CURRENT_DATE_TIME::incrementAndGet, TimeUnit.MILLISECONDS);

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void TEST_1()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT, __ -> false);

        test.await();

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        ), waitHandles);

        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_2()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        test.await();

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .indefinite(1)
                        .build(),
                WaitHandleMock.builder()
                        .indefinite(1)
                        .build()
        ), waitHandles);

        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_3()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        try {
            test.await();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ErrorComparator.create(e));
        }

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .indefinite(0)
                        .build(),
                WaitHandleMock.builder()
                        .indefinite(0)
                        .build()
        ), waitHandles);

        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_4()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        try {
            test.await();
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ErrorComparator.create(e));
        }

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .indefinite(1)
                        .build(),
                WaitHandleMock.builder()
                        .indefinite(0)
                        .build()
        ), waitHandles);

        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_5()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT, __ -> false);

        Assertions.assertTrue(test.await(10L, TimeUnit.MILLISECONDS));

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        ), waitHandles);

        Assertions.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_6()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        Assertions.assertTrue(test.await(4L, TimeUnit.MILLISECONDS));

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(4L, TimeUnit.MILLISECONDS)))
                        .build(),
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(3L, TimeUnit.MILLISECONDS)))
                        .build()
        ), waitHandles);

        Assertions.assertEquals(3L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_7()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT, __ -> true);

        Assertions.assertTrue(test.await(4L, TimeUnit.MILLISECONDS));

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .timed(List.of(
                                new TimeUnitPair(4L, TimeUnit.MILLISECONDS),
                                new TimeUnitPair(2L, TimeUnit.MILLISECONDS)
                        ))
                        .build(),
                WaitHandleMock.builder()
                        .timed(List.of(
                                new TimeUnitPair(3L, TimeUnit.MILLISECONDS),
                                new TimeUnitPair(1L, TimeUnit.MILLISECONDS)
                        ))
                        .build()
        ), waitHandles);

        Assertions.assertEquals(5L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_8() {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 1")
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 1")
                    .build(), ErrorComparator.create(e));
        }

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .build()
        ), waitHandles);

        Assertions.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_9() {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .interruptedExceptionMessage("throw interrupted exception 2")
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        try {
            test.await(4L, TimeUnit.MILLISECONDS);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(InterruptedException.class)
                    .message("throw interrupted exception 2")
                    .build(), ErrorComparator.create(e));
        }

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(4L, TimeUnit.MILLISECONDS)))
                        .build(),
                WaitHandleMock.builder()
                        .build()
        ), waitHandles);

        Assertions.assertEquals(2L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_10()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .acquired(false)
                        .build(),
                WaitHandleMock.builder()
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        Assertions.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(4L, TimeUnit.MILLISECONDS)))
                        .build(),
                WaitHandleMock.builder()
                        .build()
        ), waitHandles);

        Assertions.assertEquals(1L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void TEST_11()
            throws InterruptedException {
        List<WaitHandleMock> waitHandles = List.of(
                WaitHandleMock.builder()
                        .build(),
                WaitHandleMock.builder()
                        .acquired(false)
                        .build()
        );

        MultiWaitHandle test = new MultiWaitHandle(waitHandles, DATE_TIME_SUPPORT);

        Assertions.assertFalse(test.await(4L, TimeUnit.MILLISECONDS));

        Assertions.assertEquals(List.of(
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(4L, TimeUnit.MILLISECONDS)))
                        .build(),
                WaitHandleMock.builder()
                        .timed(List.of(new TimeUnitPair(3L, TimeUnit.MILLISECONDS)))
                        .build()
        ), waitHandles);

        Assertions.assertEquals(2L, CURRENT_DATE_TIME.get());
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
