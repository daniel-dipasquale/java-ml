package com.dipasquale.common.time;

import com.dipasquale.common.ArgumentValidatorSupport;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface DateTimeSupport {
    long now();

    static long getTimeBucket(final long dateTime, final long bucketSize, final long bucketOffset) {
        long timeBucketProgress = (dateTime + bucketOffset) % bucketSize;
        long timeBucket = dateTime - timeBucketProgress;

        return timeBucket + bucketSize * Math.floorDiv(timeBucketProgress, bucketSize);
    }

    static long getTimeBucket(final long dateTime, final long bucketSize) {
        return getTimeBucket(dateTime, bucketSize, 0L);
    }

    default long getCurrentTimeBucket(final long bucketSize) {
        return getTimeBucket(now(), bucketSize);
    }

    TimeUnit timeUnit();

    static String format(final DateTimeFormatter dateTimeFormatter, final long dateTime, final TimeUnit timeUnit) {
        long fixedDateTime = TimeUnit.MILLISECONDS.convert(dateTime, timeUnit);
        Instant dateTimeInstant = new Date(fixedDateTime).toInstant();

        return dateTimeFormatter.format(dateTimeInstant);
    }

    String format(long dateTime);

    default String nowFormatted() {
        return format(now());
    }

    static long parse(final DateTimeFormatter dateTimeParser, final String dateTime, final TimeUnit timeUnit) {
        TemporalAccessor parsedDateTime = dateTimeParser.parse(dateTime);
        long fixedDateTime = ZonedDateTime.from(parsedDateTime).toInstant().toEpochMilli();

        return timeUnit.convert(fixedDateTime, TimeUnit.MILLISECONDS);
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
