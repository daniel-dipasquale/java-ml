package com.dipasquale.common.time;

import com.dipasquale.common.error.ErrorComparer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DateTimeSupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport TEST = new DateTimeSupportProxy(CURRENT_DATE_TIME::incrementAndGet, SI.MILLI(SI.SECOND));

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_current_time_THEN_provide_the_time_in_epoch_format() {
        Assertions.assertEquals(1L, TEST.now());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_unit_the_time_represents_THEN_provide_it() {
        Assertions.assertEquals(SI.MILLI(SI.SECOND), TEST.unit());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_converting_the_time_unit_to_the_unit_THEN_provide_it() {
        Assertions.assertEquals(SI.NANO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.NANOSECONDS));
        Assertions.assertEquals(SI.MICRO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MICROSECONDS));
        Assertions.assertEquals(SI.MILLI(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MILLISECONDS));
        Assertions.assertEquals(SI.SECOND, DateTimeSupport.getUnit(TimeUnit.SECONDS));
        Assertions.assertEquals(NonSI.MINUTE, DateTimeSupport.getUnit(TimeUnit.MINUTES));
        Assertions.assertEquals(NonSI.HOUR, DateTimeSupport.getUnit(TimeUnit.HOURS));
        Assertions.assertEquals(NonSI.DAY, DateTimeSupport.getUnit(TimeUnit.DAYS));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_converting_the_unit_to_the_time_unit_THEN_provide_it() {
        Assertions.assertEquals(TimeUnit.NANOSECONDS, DateTimeSupport.getTimeUnit(SI.NANO(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.MICROSECONDS, DateTimeSupport.getTimeUnit(SI.MICRO(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.MILLISECONDS, DateTimeSupport.getTimeUnit(SI.MILLI(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.SECONDS, DateTimeSupport.getTimeUnit(SI.SECOND));
        Assertions.assertEquals(TimeUnit.MINUTES, DateTimeSupport.getTimeUnit(NonSI.MINUTE));
        Assertions.assertEquals(TimeUnit.HOURS, DateTimeSupport.getTimeUnit(NonSI.HOUR));
        Assertions.assertEquals(TimeUnit.DAYS, DateTimeSupport.getTimeUnit(NonSI.DAY));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_unit_the_time_represents_THEN_provide_it() {
        Assertions.assertEquals(TimeUnit.MILLISECONDS, TEST.timeUnit());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_a_specific_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_specified_date_time() {
        Assertions.assertEquals(1_000L, DateTimeSupport.getTimeBucket(1_099L, 100L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_100L, DateTimeSupport.getTimeBucket(1_100L, 100L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_100L, DateTimeSupport.getTimeBucket(1_101L, 100L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_a_specific_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_specified_date_time_and_substract_the_offset() {
        Assertions.assertEquals(1_050L, DateTimeSupport.getTimeBucket(1_149L, 100L, 50L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_150L, DateTimeSupport.getTimeBucket(1_150L, 100L, 50L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_150L, DateTimeSupport.getTimeBucket(1_151L, 100L, 50L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_the_current_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_current_date_time() {
        CURRENT_DATE_TIME.set(999L);
        Assertions.assertEquals(1_000L, TEST.getCurrentTimeBucket(100L));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_formatting_the_milliseconds_since_epoch_THEN_get_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("1970-01-01T00:00:00.001", TEST.format(1L));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_formatting_the_current_date_time_since_epoch_THEN_get_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("1970-01-01T00:00:00.001", TEST.nowFormatted());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_parsing_a_date_time_in_iso_format_8601_THEN_retrieve_the_milliseconds_the_date_time_represents_since_epoch() {
        Assertions.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123Z"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01Z"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321Z"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30Z"));
        Assertions.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_parsing_a_date_time_in_unparseable_format_THEN_fail_by_throwing_an_exception() {
        try {
            TEST.parse("unparseable");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'unparseable' could not be parsed at index 0")
                    .build(), ErrorComparer.create(e));
        }
    }

    @Test
    public void GIVEN_an_instance_date_time_support_created_to_represent_milliseconds_WHEN_getting_the_current_date_time_and_the_unit_THEN_provide_the_time_and_unit_in_milliseconds() {
        DateTimeSupport test = new DateTimeSupportMilliseconds();
        long startDateTime = System.currentTimeMillis();
        long result = test.now();
        long endDateTime = System.currentTimeMillis();

        Assertions.assertTrue(startDateTime <= result);
        Assertions.assertTrue(endDateTime >= result);
        Assertions.assertEquals(SI.MILLI(SI.SECOND), test.unit());
    }

    @Test
    public void GIVEN_an_instance_date_time_support_created_to_represent_nanoseconds_WHEN_getting_the_current_date_time_and_the_unit_THEN_provide_the_time_and_unit_in_nanoseconds() {
        DateTimeSupport test = new DateTimeSupportNanoseconds();
        long startDateTime = System.nanoTime();
        long result = test.now();
        long endDateTime = System.nanoTime();

        Assertions.assertTrue(startDateTime <= result);
        Assertions.assertTrue(endDateTime >= result);
        Assertions.assertEquals(SI.NANO(SI.SECOND), test.unit());
    }
}
