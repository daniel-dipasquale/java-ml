package com.dipasquale.common;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface DateTimeSupport extends Serializable {
    long now();

    Unit<Duration> unit();

    static Unit<Duration> getUnit(final TimeUnit unit) {
        return DateTimeConstants.TIME_UNITS.get(unit);
    }

    static TimeUnit getTimeUnit(final Unit<Duration> unit) {
        return DateTimeConstants.UNITS.get(unit);
    }

    default TimeUnit timeUnit() {
        return getTimeUnit(unit());
    }

    default long getTimeFrameFor(final long dateTime, final long time, final long subtraction) {
        long timeFrameProgress = dateTime % time;
        long timeFrame = dateTime - timeFrameProgress - subtraction;

        return timeFrame + time * Math.floorDiv(timeFrameProgress + subtraction, time);
    }

    default long getTimeFrameFor(final long dateTime, final long time) {
        return getTimeFrameFor(dateTime, time, 0L);
    }

    default long getCurrentTimeFrame(final long time) {
        return getTimeFrameFor(now(), time);
    }

    default long getTimeSince(final long dateTime) {
        return now() - dateTime;
    }

    default String format(final long dateTime) {
        long dateTimeConverted = (long) unit().getConverterTo(DateTimeConstants.MILLISECONDS_UNIT)
                .convert((double) dateTime);

        Instant instant = new Date(dateTimeConverted).toInstant();

        return DateTimeConstants.DATE_TIME_FORMATTER.format(instant);
    }

    default String nowFormatted() {
        return format(now());
    }

    default long parse(final String dateTime) {
        TemporalAccessor temporalAccessor = DateTimeConstants.DATE_TIME_PARSER.parse(dateTime);

        long epochTime = ZonedDateTime.from(temporalAccessor)
                .toInstant()
                .toEpochMilli();

        return (long) DateTimeConstants.MILLISECONDS_UNIT.getConverterTo(unit())
                .convert((double) epochTime);
    }

    static DateTimeSupport create(final LongFactory factory, final Unit<Duration> unit) {
        return new DateTimeSupport() {
            @Serial
            private static final long serialVersionUID = -5933526591359752376L;

            @Override
            public long now() {
                return factory.create();
            }

            @Override
            public Unit<Duration> unit() {
                return unit;
            }
        };
    }

    static DateTimeSupport createMilliseconds() {
        return create(System::currentTimeMillis, DateTimeConstants.MILLISECONDS_UNIT);
    }

    static DateTimeSupport createNanoseconds() {
        return create(System::nanoTime, DateTimeConstants.NANOSECONDS_UNIT);
    }
}
