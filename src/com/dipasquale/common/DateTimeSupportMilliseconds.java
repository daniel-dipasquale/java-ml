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
final class DateTimeSupportMilliseconds implements DateTimeSupport {
    @Serial
    private static final long serialVersionUID = 526767263817809164L;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeParser;

    DateTimeSupportMilliseconds() {
        this(DateTimeConstants.DATE_TIME_FORMATTER, DateTimeConstants.DATE_TIME_PARSER);
    }

    @Override
    public long now() {
        return System.currentTimeMillis();
    }

    @Override
    public Unit<Duration> unit() {
        return DateTimeConstants.MILLISECONDS_UNIT;
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
