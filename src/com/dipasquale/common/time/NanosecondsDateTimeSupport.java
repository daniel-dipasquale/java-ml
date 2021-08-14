package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
public final class NanosecondsDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = -6749148050121918495L;

    public NanosecondsDateTimeSupport() {
        super(Constants.NANOSECONDS_UNIT);
    }

    public NanosecondsDateTimeSupport(final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(Constants.NANOSECONDS_UNIT, dateTimeFormatter, dateTimeParser);
    }

    @Override
    public long now() {
        return System.nanoTime();
    }
}
