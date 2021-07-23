package com.dipasquale.common.time;

import java.io.Serial;

final class ExpirySupportDefault implements ExpirySupport {
    @Serial
    private static final long serialVersionUID = 4557523191903500546L;
    private final DateTimeSupport dateTimeSupport;
    private final long expiryTime;
    private final double expiryTimeDouble;
    private final long offset;
    private final double rounder;

    ExpirySupportDefault(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset, final boolean rounded) {
        this.dateTimeSupport = dateTimeSupport;
        this.expiryTime = expiryTime;
        this.expiryTimeDouble = (double) expiryTime;
        this.offset = offset;
        this.rounder = rounded ? 1D : 0.5D;
    }

    @Override
    public ExpiryRecord next() {
        long currentDateTime = dateTimeSupport.now();
        long expiryDateTimePrevious = DateTimeSupport.getTimeBucket(currentDateTime, expiryTime, offset);
        long expiryTimeProgress = (currentDateTime + expiryTime - offset) % expiryTime;
        long expiryDateTime = expiryDateTimePrevious + expiryTime * Math.round((expiryTimeDouble * rounder + (double) expiryTimeProgress) / expiryTimeDouble);

        return new ExpiryRecord(currentDateTime, expiryDateTime, dateTimeSupport.unit());
    }
}
