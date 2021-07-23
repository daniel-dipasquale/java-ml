package com.dipasquale.common.time;

import com.dipasquale.common.LongFactory;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface DateTimeSupport extends Serializable {
    long now();

    static long getTimeBucket(final long dateTime, final long bucketSize, final long bucketOffset) {
        long timeBucketProgress = dateTime % bucketSize;
        long timeBucket = dateTime - timeBucketProgress - bucketOffset;

        return timeBucket + bucketSize * Math.floorDiv(timeBucketProgress + bucketOffset, bucketSize);
    }

    static long getTimeBucket(final long dateTime, final long bucketSize) {
        return getTimeBucket(dateTime, bucketSize, 0L);
    }

    default long getCurrentTimeBucket(final long bucketSize) {
        return getTimeBucket(now(), bucketSize);
    }

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

    String format(long dateTime);

    default String nowFormatted() {
        return format(now());
    }

    static Instant convert(final long dateTime, final Unit<Duration> unit) {
        UnitConverter unitConverter = unit.getConverterTo(DateTimeConstants.MILLISECONDS_UNIT);
        long dateTimeConverted = (long) unitConverter.convert((double) dateTime);

        return new Date(dateTimeConverted).toInstant();
    }

    static String format(final DateTimeFormatter dateTimeFormatter, final long dateTime, final Unit<Duration> unit) {
        Instant instant = DateTimeSupport.convert(dateTime, unit);

        return dateTimeFormatter.format(instant);
    }

    long parse(String dateTime);

    static long convert(final TemporalAccessor dateTime, final Unit<Duration> unit) {
        long epochTime = ZonedDateTime.from(dateTime).toInstant().toEpochMilli();
        UnitConverter unitConverter = DateTimeConstants.MILLISECONDS_UNIT.getConverterTo(unit);

        return (long) unitConverter.convert((double) epochTime);
    }

    static long parse(final DateTimeFormatter dateTimeParser, final String dateTime, final Unit<Duration> unit) {
        TemporalAccessor dateTimeParsed = dateTimeParser.parse(dateTime);

        return DateTimeSupport.convert(dateTimeParsed, unit);
    }

    static DateTimeSupport createMilliseconds() {
        return new DateTimeSupportMilliseconds();
    }

    static DateTimeSupport createMilliseconds(final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        return new DateTimeSupportMilliseconds(dateTimeFormatter, dateTimeParser);
    }

    static DateTimeSupport createNanoseconds() {
        return new DateTimeSupportNanoseconds();
    }

    static DateTimeSupport createNanoseconds(final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        return new DateTimeSupportNanoseconds(dateTimeFormatter, dateTimeParser);
    }

    static DateTimeSupport createProxy(final LongFactory nowFactory, final Unit<Duration> unit) {
        return new DateTimeSupportProxy(nowFactory, unit);
    }

    static DateTimeSupport createProxy(final LongFactory nowFactory, final Unit<Duration> unit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        return new DateTimeSupportProxy(nowFactory, unit, dateTimeFormatter, dateTimeParser);
    }

    static DateTimeSupport createZero(final Unit<Duration> unit) {
        return new DateTimeSupportZero(unit);
    }

    static DateTimeSupport createZero(final Unit<Duration> unit, final String nowFormatted) {
        return new DateTimeSupportZero(unit, nowFormatted);
    }
}
