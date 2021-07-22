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
final class DateTimeSupportProxy implements DateTimeSupport {
    @Serial
    private static final long serialVersionUID = -51819225872192012L;
    private final LongFactory nowFactory;
    private final Unit<Duration> unit;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeParser;

    DateTimeSupportProxy(final LongFactory nowFactory, final Unit<Duration> unit) {
        this(nowFactory, unit, DateTimeConstants.DATE_TIME_FORMATTER, DateTimeConstants.DATE_TIME_PARSER);
    }

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
        return DateTimeSupport.format(dateTimeFormatter, dateTime, unit());
    }

    @Override
    public long parse(final String dateTime) {
        return DateTimeSupport.parse(dateTimeParser, dateTime, unit());
    }
}
