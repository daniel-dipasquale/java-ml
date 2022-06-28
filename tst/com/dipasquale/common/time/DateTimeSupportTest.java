package com.dipasquale.common.time;

import com.dipasquale.common.error.ErrorSnapshot;
import com.dipasquale.common.factory.LongFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DateTimeSupportTest {
    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
    private static final DateTimeSupportMock TEST = new DateTimeSupportMock(CURRENT_DATE_TIME::incrementAndGet, TimeUnit.MILLISECONDS);

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
    public void GIVEN_a_date_time_in_epoch_format_WHEN_formatting_it_THEN_get_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("Value(YearOfEra,4,19,EXCEEDS_PAD)'-'Value(MonthOfYear,2)'-'Value(DayOfMonth,2)'T'Value(HourOfDay,2)':'Value(MinuteOfHour,2)':'Value(SecondOfMinute,2)'.'Fraction(NanoOfSecond,3,3)", Constants.DATE_TIME_FORMATTER.toString());
        Assertions.assertEquals("1970-01-01T00:00:00.001", DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, 1L, TimeUnit.MILLISECONDS));
        Assertions.assertEquals("1970-01-01T00:00:01.000", DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, 1_000L, TimeUnit.MILLISECONDS));
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
        Assertions.assertEquals(123L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:00.123", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(123L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:00.123Z", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:01", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(1_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01T00:00:01Z", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(321L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:00.321", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(321L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:00.321Z", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(30_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:30", TimeUnit.MILLISECONDS));
        Assertions.assertEquals(30_000L, DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "1970-01-01 00:00:30Z", TimeUnit.MILLISECONDS));

        try {
            DateTimeSupport.parse(Constants.DATE_TIME_PARSER, "invalid format", TimeUnit.MILLISECONDS);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'invalid format' could not be parsed at index 0")
                    .build(), ErrorSnapshot.create(e));
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
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'invalid format' could not be parsed at index 0")
                    .build(), ErrorSnapshot.create(e));
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
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketSize '0' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.createBucketExpirationFactory(100L, -1L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '-1' cannot be less than '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.createBucketExpirationFactory(100L, 101L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '101' cannot be greater than or equal to '100'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(0L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketSize '0' cannot be less than or equal to '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(100L, -1L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '-1' cannot be less than '0'")
                    .build(), ErrorSnapshot.create(e));
        }

        try {
            TEST.createRoundedBucketExpirationFactory(100L, 101L);
            Assertions.fail();
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(IllegalArgumentException.class)
                    .message("bucketOffset '101' cannot be greater than or equal to '100'")
                    .build(), ErrorSnapshot.create(e));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DateTimeSupportMock implements DateTimeSupport, Serializable {
        @Serial
        private static final long serialVersionUID = 5818961090041503787L;
        private final LongFactory nowFactory;
        private final TimeUnit timeUnit;

        @Override
        public long now() {
            return nowFactory.create();
        }

        @Override
        public TimeUnit timeUnit() {
            return timeUnit;
        }

        @Override
        public String format(final long dateTime) {
            return DateTimeSupport.format(Constants.DATE_TIME_FORMATTER, dateTime, timeUnit);
        }

        @Override
        public long parse(final String dateTime) {
            return DateTimeSupport.parse(Constants.DATE_TIME_PARSER, dateTime, timeUnit);
        }
    }
}
