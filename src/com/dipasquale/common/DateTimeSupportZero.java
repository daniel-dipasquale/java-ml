package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DateTimeSupportZero implements DateTimeSupport {
    @Serial
    private static final long serialVersionUID = 4443590179167707368L;
    private static final String NOW_FORMATTED = "1970-01-01T00:00:00.000";
    private final Unit<Duration> unit;
    private final String nowFormatted;

    DateTimeSupportZero(final Unit<Duration> unit) {
        this(unit, NOW_FORMATTED);
    }

    @Override
    public long now() {
        return 0L;
    }

    @Override
    public Unit<Duration> unit() {
        return unit;
    }

    @Override
    public String format(final long dateTime) {
        ArgumentValidatorSupport.ensureEqual(dateTime, 0L, "dateTime", "must be 0");

        return nowFormatted;
    }

    @Override
    public long parse(final String dateTime) {
        ArgumentValidatorSupport.ensureEqual(dateTime, nowFormatted, "dateTime", String.format("must be '%s'", nowFormatted));

        return 0L;
    }
}
