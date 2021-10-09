package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
public final class MillisecondsDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 7552571945900875884L;

    public MillisecondsDateTimeSupport() {
        super(TimeUnit.MILLISECONDS);
    }

    public MillisecondsDateTimeSupport(final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(TimeUnit.MILLISECONDS, dateTimeFormatter, dateTimeParser);
    }

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
