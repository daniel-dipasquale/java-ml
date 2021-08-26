package com.dipasquale.common.time;

import com.dipasquale.common.error.ErrorComparator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class AbstractDateTimeSupportTest {
    private static final long NOW = 0L;
    private static final Unit<Duration> UNIT = SI.MILLI(SI.SECOND);
    private static final AbstractDateTimeSupport TEST = new AbstractDateTimeSupportMock(NOW, UNIT);

    @Test
    public void GIVEN_a_date_time_support_mock_WHEN_getting_the_current_date_time_THEN_provide_it_in_epoch_format() {
        Assertions.assertEquals(0L, TEST.now());
    }

    @Test
    public void GIVEN_a_date_time_support_mock_WHEN_getting_the_unit_THEN_provide_the_millisecond_unit() {
        Assertions.assertEquals(SI.MILLI(SI.SECOND), TEST.unit());
    }

    @Test
    public void GIVEN_a_date_time_support_mock_and_a_date_time_in_epoch_format_WHEN_formatting_the_date_time_into_text_THEN_format_the_date_time_it_represents_in_text_format() {
        Assertions.assertEquals("1970-01-01T00:00:00.001", TEST.format(1L));
        Assertions.assertEquals("1970-01-01T00:00:01.000", TEST.format(1_000L));
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
            Assertions.assertEquals(ErrorComparator.builder()
                    .type(DateTimeParseException.class)
                    .message("Text 'invalid format' could not be parsed at index 0")
                    .build(), ErrorComparator.create(e));
        }
    }

    private static final class AbstractDateTimeSupportMock extends AbstractDateTimeSupport {
        @Serial
        private static final long serialVersionUID = -5476714192142099507L;
        private final long now;

        private AbstractDateTimeSupportMock(final long now, final Unit<Duration> unit) {
            super(unit);
            this.now = now;
        }

        private AbstractDateTimeSupportMock(final long now, final Unit<Duration> unit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
            super(unit, dateTimeFormatter, dateTimeParser);
            this.now = now;
        }

        @Override
        public long now() {
            return now;
        }
    }
}
