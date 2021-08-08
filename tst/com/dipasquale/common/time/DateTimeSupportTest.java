package com.dipasquale.common.time;

import com.dipasquale.common.error.ErrorComparer;
import com.dipasquale.common.factory.LongFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DateTimeSupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupportMock TEST = new DateTimeSupportMock(CURRENT_DATE_TIME::incrementAndGet, SI.MILLI(SI.SECOND));

    @BeforeEach
    public void beforeEach() {
        CURRENT_DATE_TIME.set(0L);
    }

    @Test
    public void GIVEN_a_date_time_in_epoch_format_and_a_bucket_size_and_sometimes_a_bucket_offset_WHEN_calculating_the_time_bucket_THEN_assign_the_given_date_time_to_a_date_time_in_the_past_or_the_present() {
        Assertions.assertEquals(1_000L, DateTimeSupport.getTimeBucket(1_099L, 100L));
        Assertions.assertEquals(1_100L, DateTimeSupport.getTimeBucket(1_100L, 100L));
        Assertions.assertEquals(1_100L, DateTimeSupport.getTimeBucket(1_101L, 100L));
        Assertions.assertEquals(1_050L, DateTimeSupport.getTimeBucket(1_149L, 100L, 50L));
        Assertions.assertEquals(1_150L, DateTimeSupport.getTimeBucket(1_150L, 100L, 50L));
        Assertions.assertEquals(1_150L, DateTimeSupport.getTimeBucket(1_151L, 100L, 50L));
    }

    @Test
    public void GIVEN_a_date_time_support_mock_and_a_bucket_size_WHEN_calculating_the_current_time_bucket_THEN_assign_the_given_date_time_to_a_date_time_in_the_past_or_the_present() {
        CURRENT_DATE_TIME.set(1_098L);
        Assertions.assertEquals(1_000L, TEST.getCurrentTimeBucket(100L));
        Assertions.assertEquals(1_099L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_100L, TEST.getCurrentTimeBucket(100L));
        Assertions.assertEquals(1_100L, CURRENT_DATE_TIME.get());
        Assertions.assertEquals(1_100L, TEST.getCurrentTimeBucket(100L));
        Assertions.assertEquals(1_101L, CURRENT_DATE_TIME.get());
    }

    @Test
    public void GIVEN_a_time_unit_WHEN_converting_to_the_unit_type_THEN_convert_it() {
        Assertions.assertEquals(SI.NANO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.NANOSECONDS));
        Assertions.assertEquals(SI.MICRO(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MICROSECONDS));
        Assertions.assertEquals(SI.MILLI(SI.SECOND), DateTimeSupport.getUnit(TimeUnit.MILLISECONDS));
        Assertions.assertEquals(SI.SECOND, DateTimeSupport.getUnit(TimeUnit.SECONDS));
        Assertions.assertEquals(NonSI.MINUTE, DateTimeSupport.getUnit(TimeUnit.MINUTES));
        Assertions.assertEquals(NonSI.HOUR, DateTimeSupport.getUnit(TimeUnit.HOURS));
        Assertions.assertEquals(NonSI.DAY, DateTimeSupport.getUnit(TimeUnit.DAYS));
    }

    @Test
    public void GIVEN_a_unit_WHEN_converting_to_the_time_unit_type_THEN_convert_it() {
        Assertions.assertEquals(TimeUnit.NANOSECONDS, DateTimeSupport.getTimeUnit(SI.NANO(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.MICROSECONDS, DateTimeSupport.getTimeUnit(SI.MICRO(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.MILLISECONDS, DateTimeSupport.getTimeUnit(SI.MILLI(SI.SECOND)));
        Assertions.assertEquals(TimeUnit.SECONDS, DateTimeSupport.getTimeUnit(SI.SECOND));
        Assertions.assertEquals(TimeUnit.MINUTES, DateTimeSupport.getTimeUnit(NonSI.MINUTE));
        Assertions.assertEquals(TimeUnit.HOURS, DateTimeSupport.getTimeUnit(NonSI.HOUR));
        Assertions.assertEquals(TimeUnit.DAYS, DateTimeSupport.getTimeUnit(NonSI.DAY));
    }

    @Test
    public void GIVEN_a_date_time_in_epoch_format_WHEN_formatting_it_THEN_get_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("Value(YearOfEra,4,19,EXCEEDS_PAD)'-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)'T'Value(HourOfDay,2)':'Value(MinuteOfHour,2)':'Value(SecondOfMinute,2)'.'Fraction(NanoOfSecond,3,3)", Constants.DATE_TIME_FORMATTER.toString());
        Assertions.assertEquals("1970-01-01T00:00:00.001", DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, 1L, SI.MILLI(SI.SECOND)));
        Assertions.assertEquals("1970-01-01T00:00:01.000", DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, 1_000L, SI.MILLI(SI.SECOND)));
    }

    @Test
    public void GIVEN_a_date_time_support_mock_and_a_date_time_in_epoch_format_WHEN_formatting_the_date_time_into_text_THEN_format_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("1970-01-01T00:00:00.001", TEST.format(1L));
        Assertions.assertEquals("1970-01-01T00:00:01.000", TEST.format(1_000L));
    }

    @Test
    public void GIVEN_a_proxy_date_time_support_WHEN_formatting_the_current_date_time_THEN_get_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("1970-01-01T00:00:00.001", TEST.nowFormatted());
    }

    @Test
    public void GIVEN_a_text_WHEN_parsing_it_using_the_date_time_iso_format_8601_THEN_either_parse_it_into_epoch_format_if_valid_otherwise_fail_by_throwing_a_date_time_parse_exception() {
        Assertions.assertEquals("Value(YearOfEra,4,19,EXCEEDS_PAD)'-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)[[' ']['T']Value(HourOfDay,2)':'Value(MinuteOfHour,2)[':'Value(SecondOfMinute,2)['.'Fraction(NanoOfSecond,3,3)][ZoneText(SHORT)][Offset(+HHMM,'+0000')]]]", Constants.DATE_TIME_PARSER.toString());
        Assertions.assertEquals(123L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:00.123", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(123L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:00.123Z", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(1_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:01", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(1_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:01Z", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(321L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:00.321", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(321L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:00.321Z", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(30_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:30", Constants.MILLISECONDS_UNIT));
        Assertions.assertEquals(30_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:30Z", Constants.MILLISECONDS_UNIT));

        try {
            DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "invalid format", Constants.MILLISECONDS_UNIT);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'invalid format' could not be parsed at index 0")
                    .build(), ErrorComparer.create(e));
        }
    }

    @Test
    public void GIVEN_a_date_time_support_mock_and_a_text_WHEN_parsing_it_using_the_date_time_iso_format_8601_THEN_either_parse_it_into_epoch_format_if_valid_otherwise_fail_by_throwing_a_date_time_parse_exception() {
        Assertions.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123"));
        Assertions.assertEquals(123L, TEST.parse("1970-01-01T00:00:00.123Z"));
        Assertions.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01"));
        Assertions.assertEquals(1_000L, TEST.parse("1970-01-01T00:00:01Z"));
        Assertions.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321"));
        Assertions.assertEquals(321L, TEST.parse("1970-01-01 00:00:00.321Z"));
        Assertions.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30"));
        Assertions.assertEquals(30_000L, TEST.parse("1970-01-01 00:00:30Z"));

        try {
            TEST.parse("invalid format");
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'invalid format' could not be parsed at index 0")
                    .build(), ErrorComparer.create(e));
        }
    }

    @Test
    public void GIVEN_a_date_time_support_mock_and_an_expiration_bucket_size_and_offset_WHEN_creating_an_instance_of_the_bucket_expiration_factory_THEN_create_it_with_the_expected_bucket_size_and_offset() {
        Assertions.assertEquals(new BucketExpirationFactory(TEST, 100L, 0L, false), TEST.createBucketExpirationFactory(100L));
        Assertions.assertNotEquals(new BucketExpirationFactory(TEST, 100L, 0L, false), TEST.createBucketExpirationFactory(1L));
        Assertions.assertEquals(new BucketExpirationFactory(TEST, 100L, 1L, false), TEST.createBucketExpirationFactory(100L, 1L));
        Assertions.assertNotEquals(new BucketExpirationFactory(TEST, 100L, 1L, false), TEST.createBucketExpirationFactory(100L, 2L));
        Assertions.assertEquals(new BucketExpirationFactory(TEST, 100L, 0L, true), TEST.createRoundedBucketExpirationFactory(100L));
        Assertions.assertNotEquals(new BucketExpirationFactory(TEST, 100L, 0L, true), TEST.createRoundedBucketExpirationFactory(1L));
        Assertions.assertEquals(new BucketExpirationFactory(TEST, 100L, 1L, true), TEST.createRoundedBucketExpirationFactory(100L, 1L));
        Assertions.assertNotEquals(new BucketExpirationFactory(TEST, 100L, 1L, true), TEST.createRoundedBucketExpirationFactory(100L, 2L));

        try {
            TEST.createBucketExpirationFactory(0L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketSize '0' cannot be less than or equal to '0'")
                    .build(), ErrorComparer.create(e));
        }

        try {
            TEST.createBucketExpirationFactory(100L, -1L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '-1' cannot be less than '0'")
                    .build(), ErrorComparer.create(e));
        }

        try {
            TEST.createBucketExpirationFactory(100L, 101L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '101' cannot be greater than or equal to '100'")
                    .build(), ErrorComparer.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(0L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketSize '0' cannot be less than or equal to '0'")
                    .build(), ErrorComparer.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(100L, -1L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '-1' cannot be less than '0'")
                    .build(), ErrorComparer.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(100L, 101L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorComparer.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '101' cannot be greater than or equal to '100'")
                    .build(), ErrorComparer.create(e));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DateTimeSupportMock implements DateTimeSupport, Serializable {
        @Serial
        private static final long serialVersionUID = 5818961090041503787L;
        private final LongFactory nowFactory;
        private final Unit<Duration> unit;

        @Override
        public long now() {
            return nowFactory.create();
        }

        @Override
        public Unit<Duration> unit() {
            return unit;
        }

        @Override
        public String format(final long dateTime) {
            return DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, dateTime, unit);
        }

        @Override
        public long parse(final String dateTime) {
            return DateTimeSupport.parse(Constants.DATE_TIME_PARSER, dateTime, unit);
        }
    }
}
