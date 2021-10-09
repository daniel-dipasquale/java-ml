package com.dipasquale.common.time;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
abstract class AbstractDateTimeSupport implements DateTimeSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 8555314249316920593L;
    private final TimeUnit timeUnit;
    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter dateTimeParser;

    protected AbstractDateTimeSupport(final TimeUnit timeUnit) {
        this(timeUnit, Constants.DATE_TIME_FORMATTER, Constants.DATE_TIME_PARSER);
    }

    @Override
    public final TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public final String format(final long dateTime) {
        return DateTimeSupport.format(dateTimeFormatter, dateTime, timeUnit);
    }

    @Override
    public final long parse(final String dateTime) {
        return DateTimeSupport.parse(dateTimeParser, dateTime, timeUnit);
    }
}
