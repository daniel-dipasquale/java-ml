package com.dipasquale.common.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public final class NanosecondsDateTimeSupportTest {
    private static final NanosecondsDateTimeSupport TEST = new NanosecondsDateTimeSupport();

    @Test
    public void GIVEN_a_nanoseconds_date_time_support_WHEN_doing_minimal_testing_THEN_check_if_it_extends_abstract_date_time_support() {
        Assertions.assertTrue(TEST instanceof AbstractDateTimeSupport);
    }

    private static void assertInBetween(final long minimum, final long value, final long maximum) {
        Assertions.assertTrue(minimum <= value);
        Assertions.assertTrue(maximum >= value);
    }

    @Test
    public void GIVEN_a_nanoseconds_date_time_support_WHEN_getting_the_current_date_time_THEN_provide_it_in_epoch_format() {
        assertInBetween(System.nanoTime(), TEST.now(), System.nanoTime());
    }

    @Test
    public void GIVEN_a_nanoseconds_date_time_support_WHEN_getting_the_unit_measuring_the_current_date_time_THEN_provide_it() {
        Assertions.assertEquals(TimeUnit.NANOSECONDS, TEST.timeUnit());
    }

    @Test
    public void GIVEN_two_instances_of_nanoseconds_date_time_support_WHEN_comparing_whether_they_are_equal_THEN_indicate_they_are_equal_if_they_were_initialized_the_same_way_otherwise_indicate_they_are_not() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM");

        Assertions.assertEquals(new NanosecondsDateTimeSupport(), TEST);
        Assertions.assertNotEquals(new NanosecondsDateTimeSupport(formatter, parser), TEST);
        Assertions.assertEquals(new NanosecondsDateTimeSupport(Constants.DATE_TIME_FORMATTER, Constants.DATE_TIME_PARSER), TEST);
    }
}
