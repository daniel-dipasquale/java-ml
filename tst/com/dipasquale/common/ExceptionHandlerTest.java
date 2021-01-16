package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableAsserter;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public class ExceptionHandlerTest {
    @Test
    public void TEST_1() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<Handler> items = ImmutableList.<Handler>builder()
                .add(new Handler(data, 0, 3))
                .add(new Handler(data, 1, 2))
                .add(new Handler(data, 2, 1))
                .build();

        ExceptionHandler test = ExceptionHandler.create(items, Handler::handle);

        test.invokeAllAndThrowAsSuppressedIfAny(() -> new RuntimeException("unit test failure"));
        Assert.assertEquals(3, data.get(0));
        Assert.assertEquals(2, data.get(1));
        Assert.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_2() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<Handler> items = ImmutableList.<Handler>builder()
                .add(new Handler(data, 0, 3))
                .add(new Handler(data, -1, 2))
                .add(new Handler(data, 2, 1))
                .build();

        ExceptionHandler test = ExceptionHandler.create(items, Handler::handle);

        try {
            test.invokeAllAndThrowAsSuppressedIfAny(() -> new RuntimeException("unit test failure"));
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(RuntimeException.class)
                    .message("unit test failure")
                    .suppressed(ImmutableList.<ThrowableAsserter>builder()
                            .add(ThrowableAsserter.builder()
                                    .type(ArrayIndexOutOfBoundsException.class)
                                    .message("Index -1 out of bounds for length 3")
                                    .build())
                            .build())
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(3, data.get(0));
        Assert.assertEquals(0, data.get(1));
        Assert.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_3() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<Handler> items = ImmutableList.<Handler>builder()
                .add(new Handler(data, 0, 3))
                .add(new Handler(data, 1, 2))
                .add(new Handler(data, 2, 1))
                .build();

        ExceptionHandler test = ExceptionHandler.create(items, Handler::handle);

        test.invokeAllAndThrowAsSuppressedIfAny("unit test failure");
        Assert.assertEquals(3, data.get(0));
        Assert.assertEquals(2, data.get(1));
        Assert.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_4() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<Handler> items = ImmutableList.<Handler>builder()
                .add(new Handler(data, 0, 3))
                .add(new Handler(data, -1, 2))
                .add(new Handler(data, 2, 1))
                .build();

        ExceptionHandler test = ExceptionHandler.create(items, Handler::handle);

        try {
            test.invokeAllAndThrowAsSuppressedIfAny("unit test failure");
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(RuntimeException.class)
                    .message("unit test failure")
                    .suppressed(ImmutableList.<ThrowableAsserter>builder()
                            .add(ThrowableAsserter.builder()
                                    .type(ArrayIndexOutOfBoundsException.class)
                                    .message("Index -1 out of bounds for length 3")
                                    .build())
                            .build())
                    .build(), ThrowableAsserter.create(e));
        }

        Assert.assertEquals(3, data.get(0));
        Assert.assertEquals(0, data.get(1));
        Assert.assertEquals(1, data.get(2));
    }

    @RequiredArgsConstructor
    private static final class Handler {
        private final AtomicLongArray data;
        private final int index;
        private final long value;

        public void handle() {
            data.set(index, value);
        }
    }
}
