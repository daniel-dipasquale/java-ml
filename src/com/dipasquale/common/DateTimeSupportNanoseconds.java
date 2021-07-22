package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.time.format.DateTimeFormatter;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DateTimeSupportNanoseconds implements DateTimeSupport {
    @Serial
    private static final long serialVersionUID = 3025659867976964745L;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeParser;

    DateTimeSupportNanoseconds() {
        this(DateTimeConstants.DATE_TIME_FORMATTER, DateTimeConstants.DATE_TIME_PARSER);
    }

    @Override
    public long now() {
        return System.nanoTime();
    }

    @Override
    public Unit<Duration> unit() {
        return DateTimeConstants.NANOSECONDS_UNIT;
    }

    @Override
    public String format(final long dateTime) {
        return DateTimeSupport.format(dateTimeFormatter, dateTime, unit());
    }

    @Override
    public long parse(final String dateTime) {
        return DateTimeSupport.parse(dateTimeParser, dateTime, unit());
    }
}
