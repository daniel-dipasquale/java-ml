/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode
final class BucketExpirationFactory implements ExpirationFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 4557523191903500546L;
    private final DateTimeSupport dateTimeSupport;
    private final long bucketSize;
    private final double bucketSizeDouble;
    private final long bucketOffset;
    private final double roundingRate;

    BucketExpirationFactory(final DateTimeSupport dateTimeSupport, final long bucketSize, final long bucketOffset, final boolean rounded) {
        this.dateTimeSupport = dateTimeSupport;
        this.bucketSize = bucketSize;
        this.bucketSizeDouble = (double) bucketSize;
        this.bucketOffset = bucketOffset;
        this.roundingRate = rounded ? 1D : 0.5D;
    }

    @Override
    public ExpirationRecord create() {
        long currentDateTime = dateTimeSupport.now();
        long expirationDateTimePrevious = DateTimeSupport.getTimeBucket(currentDateTime, bucketSize, bucketOffset);
        long bucketSizeProgressRate = (currentDateTime + bucketSize - bucketOffset) % bucketSize;
        long expirationDateTime = expirationDateTimePrevious + bucketSize * Math.round((bucketSizeDouble * roundingRate + (double) bucketSizeProgressRate) / bucketSizeDouble);

        return new ExpirationRecord(currentDateTime, expirationDateTime, dateTimeSupport.unit());
    }
}
