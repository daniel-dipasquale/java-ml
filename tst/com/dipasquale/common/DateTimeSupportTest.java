package com.dipasquale.common;

import com.dipasquale.common.test.ThrowableComparer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DateTimeSupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupport TEST = DateTimeSupport.create(CURRENT_DATE_TIME::incrementAndGet, SI.MILLI(SI.SECOND));

    @Before
    public void before() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_current_time_THEN_provide_the_time_in_epoch_format() {
        Assert.assertEquals(1L, TEST.now());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_unit_the_time_represents_THEN_provide_it() {
        Assert.assertEquals(SI.MILLI(SI.SECOND), TEST.unit());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_converting_the_time_unit_to_the_unit_THEN_provide_it() {
        Assert.assertEquals(SI.NANO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.NANOSECONDS));
        Assert.assertEquals(SI.MICRO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MICROSECONDS));
        Assert.assertEquals(SI.MILLI(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MILLISECONDS));
        Assert.assertEquals(SI.SECOND, DateTimeSupport.getUnit(TimeUnit.SECONDS));
        Assert.assertEquals(NonSI.MINUTE, DateTimeSupport.getUnit(TimeUnit.MINUTES));
        Assert.assertEquals(NonSI.HOUR, DateTimeSupport.getUnit(TimeUnit.HOURS));
        Assert.assertEquals(NonSI.DAY, DateTimeSupport.getUnit(TimeUnit.DAYS));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_converting_the_unit_to_the_time_unit_THEN_provide_it() {
        Assert.assertEquals(TimeUnit.NANOSECONDS, DateTimeSupport.getTimeUnit(SI.NANO(SI.SECOND)));
        Assert.assertEquals(TimeUnit.MICROSECONDS, DateTimeSupport.getTimeUnit(SI.MICRO(SI.SECOND)));
        Assert.assertEquals(TimeUnit.MILLISECONDS, DateTimeSupport.getTimeUnit(SI.MILLI(SI.SECOND)));
        Assert.assertEquals(TimeUnit.SECONDS, DateTimeSupport.getTimeUnit(SI.SECOND));
        Assert.assertEquals(TimeUnit.MINUTES, DateTimeSupport.getTimeUnit(NonSI.MINUTE));
        Assert.assertEquals(TimeUnit.HOURS, DateTimeSupport.getTimeUnit(NonSI.HOUR));
        Assert.assertEquals(TimeUnit.DAYS, DateTimeSupport.getTimeUnit(NonSI.DAY));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_unit_the_time_represents_THEN_provide_it() {
        Assert.assertEquals(TimeUnit.MILLISECONDS, TEST.timeUnit());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_a_specific_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_specified_date_time() {
        Assert.assertEquals(1_000L, TEST.getTimeFrameFor(1_099L, 100L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_100L, TEST.getTimeFrameFor(1_100L, 100L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_100L, TEST.getTimeFrameFor(1_101L, 100L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_a_specific_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_specified_date_time_and_substract_the_offset() {
        Assert.assertEquals(1_050L, TEST.getTimeFrameFor(1_149L, 100L, 50L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_150L, TEST.getTimeFrameFor(1_150L, 100L, 50L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_150L, TEST.getTimeFrameFor(1_151L, 100L, 50L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_frame_for_the_current_date_time_THEN_remove_the_remainder_of_the_time_past_the_division_of_the_current_date_time() {
        CURRENT_DATE_TIME.set(999L);
        Assert.assertEquals(1_000L, TEST.getCurrentTimeFrame(100L));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_getting_the_time_spent_since_the_specified_date_time_THEN_provide_the_time_difference() {
        Assert.assertEquals(0L, TEST.getTimeSince(1L));
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_formatting_the_milliseconds_since_epoch_THEN_get_the_date_time_it_represents_in_text_format() {
        Assert.assertEquals("1970-01-01T00:00:00.001", TEST.format(1L));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_formatting_the_current_date_time_since_epoch_THEN_get_the_date_time_it_represents_in_text_format() {
        Assert.assertEquals("1970-01-01T00:00:00.001", TEST.nowFormatted());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_parsing_a_date_time_in_iso_format_8601_THEN_retrieve_the_milliseconds_the_date_time_represents_since_epoch() {
        Assert.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123Z"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01Z"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321Z"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
        Assert.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30Z"));
        Assert.assertEquals(0L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_an_instance_of_the_date_time_support_WHEN_parsing_a_date_time_in_unparseable_format_THEN_fail_by_throwing_an_exception() {
        try {
            TEST.parse("unparseable");
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertEquals(ThrowableComparer.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'unparseable' could not be parsed at index 0")
                    .build(), ThrowableComparer.create(e));
        }
    }

    @Test
    public void GIVEN_an_instance_date_time_support_created_to_represent_milliseconds_WHEN_getting_the_current_date_time_and_the_unit_THEN_provide_the_time_and_unit_in_milliseconds() {
        DateTimeSupport test = DateTimeSupport.createMilliseconds();
        long startDateTime = System.currentTimeMillis();
        long result = test.now();
        long endDateTime = System.currentTimeMillis();

        Assert.assertTrue(startDateTime <= result);
        Assert.assertTrue(endDateTime >= result);
        Assert.assertEquals(SI.MILLI(SI.SECOND), test.unit());
    }

    @Test
    public void GIVEN_an_instance_date_time_support_created_to_represent_nanoseconds_WHEN_getting_the_current_date_time_and_the_unit_THEN_provide_the_time_and_unit_in_nanoseconds() {
        DateTimeSupport test = DateTimeSupport.createNanoseconds();
        long startDateTime = System.nanoTime();
        long result = test.now();
        long endDateTime = System.nanoTime();

        Assert.assertTrue(startDateTime <= result);
        Assert.assertTrue(endDateTime >= result);
        Assert.assertEquals(SI.NANO(SI.SECOND), test.unit());
    }
}
