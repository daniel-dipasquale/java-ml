package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableComparer;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ExceptionHandlerSupportTest {
    private static final ExceptionHandlerSupport TEST = ExceptionHandlerSupport.getInstance();

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
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(IllegalStateException.class)
                    .message("test-message")
                    .suppressed(ImmutableList.<ThrowableComparer>builder()
                            .add(ThrowableComparer.builder()
                                    .type(IllegalStateException.class)
                                    .message("illegal-state-exception")
                                    .build())
                            .build())
                    .build(), ThrowableComparer.create(e));
        }
    }

    @Test
    public void GIVEN_there_are_suppressed_exceptions_WHEN_wrapping_them_in_a_runtime_exception_THEN_fail_with_a_runtime_exception_wrapping_all_other_exceptions_as_suppressed_in_it() {
        try {
            TEST.throwAsSuppressedIfAny("test-message", ImmutableList.of(new IllegalStateException("illegal-state-exception")));
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(RuntimeException.class)
                    .message("test-message")
                    .suppressed(ImmutableList.<ThrowableComparer>builder()
                            .add(ThrowableComparer.builder()
                                    .type(IllegalStateException.class)
                                    .message("illegal-state-exception")
                                    .build())
                            .build())
                    .build(), ThrowableComparer.create(e));
        }
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
}
