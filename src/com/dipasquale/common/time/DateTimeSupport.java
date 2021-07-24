package com.dipasquale.common.time;

import com.dipasquale.common.ArgumentValidatorSupport;

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
        return DateTimeSupportConstants.TIME_UNITS.get(unit);
    }

    static TimeUnit getTimeUnit(final Unit<Duration> unit) {
        return DateTimeSupportConstants.UNITS.get(unit);
    }

    default TimeUnit timeUnit() {
        return getTimeUnit(unit());
    }

    static String format(final DateTimeFormatter dateTimeFormatter, final long dateTime, final Unit<Duration> unit) {
        UnitConverter unitConverter = unit.getConverterTo(DateTimeSupportConstants.MILLISECONDS_UNIT);
        long dateTimeFixed = (long) unitConverter.convert((double) dateTime);
        Instant dateTimeInstant = new Date(dateTimeFixed).toInstant();

        return dateTimeFormatter.format(dateTimeInstant);
    }

    String format(long dateTime);

    default String nowFormatted() {
        return format(now());
    }

    static long parse(final DateTimeFormatter dateTimeParser, final String dateTime, final Unit<Duration> unit) {
        UnitConverter unitConverter = DateTimeSupportConstants.MILLISECONDS_UNIT.getConverterTo(unit);
        TemporalAccessor dateTimeParsed = dateTimeParser.parse(dateTime);
        long dateTimeFixed = ZonedDateTime.from(dateTimeParsed).toInstant().toEpochMilli();

        return (long) unitConverter.convert((double) dateTimeFixed);
    }

    long parse(String dateTime);

    private static ExpirationFactory createBucketExpirationFactory(final DateTimeSupport dateTimeSupport, final long bucketSize, final long bucketOffset, final boolean rounded) {
        ArgumentValidatorSupport.ensureGreaterThanZero(bucketSize, "bucketSize");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(bucketOffset, "bucketOffset");
        ArgumentValidatorSupport.ensureLessThan(bucketOffset, bucketSize, "bucketOffset");

        return new BucketExpirationFactory(dateTimeSupport, bucketSize, bucketOffset, rounded);
    }

    default ExpirationFactory createBucketExpirationFactory(final long bucketSize, final long bucketOffset) {
        return createBucketExpirationFactory(this, bucketSize, bucketOffset, false);
    }

    default ExpirationFactory createBucketExpirationFactory(final long bucketSize) {
        return createBucketExpirationFactory(bucketSize, 0L);
    }

    default ExpirationFactory createRoundedBucketExpirationFactory(final long bucketSize, final long bucketOffset) {
        return createBucketExpirationFactory(this, bucketSize, bucketOffset, true);
    }

    default ExpirationFactory createRoundedBucketExpirationFactory(final long bucketSize) {
        return createRoundedBucketExpirationFactory(bucketSize, 0L);
    }
}
