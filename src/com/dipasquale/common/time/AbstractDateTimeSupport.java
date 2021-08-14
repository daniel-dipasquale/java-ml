package com.dipasquale.common.time;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
abstract class AbstractDateTimeSupport implements DateTimeSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 8555314249316920593L;
    private final Unit<Duration> unit;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeParser;

    protected AbstractDateTimeSupport(final Unit<Duration> unit) {
        this(unit, Constants.DATE_TIME_FORMATTER, Constants.DATE_TIME_PARSER);
    }

    @Override
    public final Unit<Duration> unit() {
        return unit;
    }

    @Override
    public final String format(final long dateTime) {
        return DateTimeSupport.format(dateTimeFormatter, dateTime, unit);
    }

    @Override
    public final long parse(final String dateTime) {
        return DateTimeSupport.parse(dateTimeParser, dateTime, unit);
    }
}
