package com.dipasquale.common;

@FunctionalInterface
public interface ExpirySupport {
    ExpiryRecord next();

    private static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset, final double slider) {
        ArgumentValidator.getInstance().ensureNotNull(dateTimeSupport, "dateTimeSupport");
        ArgumentValidator.getInstance().ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidator.getInstance().ensureGreaterThanOrEqualToZero(offset, "offset");
        ArgumentValidator.getInstance().ensureGreaterThan(expiryTime, offset, "expiryTime");

        double expiryTimeDouble = (double) expiryTime;

        return () -> {
            long currentDateTime = dateTimeSupport.now();
            long expiryDateTimePrevious = dateTimeSupport.getTimeFrameFor(currentDateTime, expiryTime, offset);
            long expiryDateTime = expiryDateTimePrevious + expiryTime * Math.round((expiryTimeDouble * slider + (double) (expiryTime + currentDateTime - offset) % expiryTime) / expiryTimeDouble);

            return new ExpiryRecord(currentDateTime, expiryDateTime, dateTimeSupport.unit());
        };
    }

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, 1D);
    }

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return createSlider(dateTimeSupport, expiryTime, 0L);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, 0.5D);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return create(dateTimeSupport, expiryTime, 0L);
    }

    static ExpirySupport.Factory createSliderFactory(final DateTimeSupport dateTimeSupport) {
        return (et, s) -> createSlider(dateTimeSupport, et, s);
    }

    static ExpirySupport.Factory createFactory(final DateTimeSupport dateTimeSupport) {
        return (et, s) -> create(dateTimeSupport, et, s);
    }

    @FunctionalInterface
    interface Factory {
        ExpirySupport create(long expiryTime, long offset);

        default ExpirySupport create(final long expiryTime) {
            return create(expiryTime, 0L);
        }
    }
}
