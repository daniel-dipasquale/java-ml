package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableAsserter;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ExceptionSupportTest {
    private static final ExceptionSupport TEST = ExceptionSupport.getInstance();

    private static Throwable createException() {
        try {
            throw new RuntimeException("cause");
        } catch (Throwable cause) {
            try {
                throw new RuntimeException("root", cause);
            } catch (Throwable root) {
                try {
                    throw new RuntimeException("suppressed");
                } catch (Throwable suppressed) {
                    root.addSuppressed(suppressed);
                }

                return root;
            }
        }
    }

    @Test
    public void GIVEN_there_are_no_suppressed_exceptions_WHEN_attempting_to_wrap_them_in_a_custom_exception_THEN_avoid_creating_the_exception() {
        TEST.throwAsSuppressedIfAny(() -> new IllegalStateException("test-message"), ImmutableList.of());
    }

    @Test
    public void GIVEN_there_are_no_suppressed_exceptions_WHEN_attempting_to_wrap_them_in_a_runtime_exception_THEN_avoid_creating_the_exception() {
        TEST.throwAsSuppressedIfAny("test-message", ImmutableList.of());
    }

    @Test
    public void GIVEN_there_are_suppressed_exceptions_WHEN_wrapping_them_in_a_custom_exception_THEN_fail_with_the_custom_exception_wrapping_all_other_exceptions_as_suppressed_in_it() {
        try {
            TEST.throwAsSuppressedIfAny(() -> new IllegalStateException("test-message"), ImmutableList.of(new IllegalStateException("illegal-state-exception")));
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(IllegalStateException.class)
                    .message("test-message")
                    .suppressed(ImmutableList.<ThrowableAsserter>builder()
                            .add(ThrowableAsserter.builder()
                                    .type(IllegalStateException.class)
                                    .message("illegal-state-exception")
                                    .build())
                            .build())
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void GIVEN_there_are_suppressed_exceptions_WHEN_wrapping_them_in_a_runtime_exception_THEN_fail_with_a_runtime_exception_wrapping_all_other_exceptions_as_suppressed_in_it() {
        try {
            TEST.throwAsSuppressedIfAny("test-message", ImmutableList.of(new IllegalStateException("illegal-state-exception")));
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableAsserter.builder()
                    .type(RuntimeException.class)
                    .message("test-message")
                    .suppressed(ImmutableList.<ThrowableAsserter>builder()
                            .add(ThrowableAsserter.builder()
                                    .type(IllegalStateException.class)
                                    .message("illegal-state-exception")
                                    .build())
                            .build())
                    .build(), ThrowableAsserter.create(e));
        }
    }

    @Test
    public void TEST_1() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<Handler> items = ImmutableList.<Handler>builder()
                .add(new Handler(data, 0, 3))
                .add(new Handler(data, 1, 2))
                .add(new Handler(data, 2, 1))
                .build();

        TEST.invokeAllAndThrowAsSuppressedIfAny(items, Handler::handle, () -> new RuntimeException("unit test failure"));
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

        try {
            TEST.invokeAllAndThrowAsSuppressedIfAny(items, Handler::handle, () -> new RuntimeException("unit test failure"));
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

        TEST.invokeAllAndThrowAsSuppressedIfAny(items, Handler::handle, "unit test failure");
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

        try {
            TEST.invokeAllAndThrowAsSuppressedIfAny(items, Handler::handle, "unit test failure");
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
    public void GIVEN_an_exception_with_a_cause_and_suppressed_exceptions_WHEN_printing_the_information_in_an_output_stream_THEN_print_all_of_it_by_relying_on_the_exception_printStackTrace_method()
            throws IOException {
        String rootRegex = "java\\.lang\\.RuntimeException: root(?:[\\s\\S](?!Suppressed))+\\s*";
        String suppressedRegex = "Suppressed: java\\.lang\\.RuntimeException: suppressed(?:[\\s\\S](?!Caused by))+\\s*";
        String causeRegex = "Caused by: java\\.lang\\.RuntimeException: cause(?:[\\s\\S](?!Caused by))+\\s*";
        String nextRegex = "Caused by: java\\.lang\\.RuntimeException: cause[\\s+\\S+]+";
        Predicate<String> assertResult = Pattern.compile(String.format("^%s%s%s%s$", rootRegex, suppressedRegex, causeRegex, nextRegex)).asPredicate();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            TEST.print(outputStream, createException(), false, StandardCharsets.UTF_8);

            String result = outputStream.toString(StandardCharsets.UTF_8);

            Assert.assertTrue(assertResult.test(result)); // TODO: FIX THIS
        }
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
