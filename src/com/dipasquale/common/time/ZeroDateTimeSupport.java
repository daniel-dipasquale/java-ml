package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
public final class ZeroDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 4537808299655330771L;

    public ZeroDateTimeSupport(final TimeUnit timeUnit) {
        super(timeUnit);
    }

    public ZeroDateTimeSupport(final TimeUnit timeUnit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(timeUnit, dateTimeFormatter, dateTimeParser);
    }

    @Override
    public long now() {
        return 0L;
    }
}
