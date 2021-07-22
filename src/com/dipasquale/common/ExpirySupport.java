package com.dipasquale.common;

import java.io.Serializable;

@FunctionalInterface
public interface ExpirySupport extends Serializable {
    ExpiryRecord next();

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return new ExpirySupportDefault(dateTimeSupport, expiryTime, offset, true);
    }

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return new ExpirySupportDefault(dateTimeSupport, expiryTime, 0L, true);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return new ExpirySupportDefault(dateTimeSupport, expiryTime, offset, false);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return new ExpirySupportDefault(dateTimeSupport, expiryTime, 0L, false);
    }

    static Factory createSliderFactory(final DateTimeSupport dateTimeSupport) {
        return new ExpirySupportFactoryDefault(dateTimeSupport, true);
    }

    static Factory createFactory(final DateTimeSupport dateTimeSupport) {
        return new ExpirySupportFactoryDefault(dateTimeSupport, false);
    }

    @FunctionalInterface
    interface Factory extends Serializable {
        ExpirySupport create(long expiryTime, long offset);

        default ExpirySupport create(final long expiryTime) {
            return create(expiryTime, 0L);
        }
    }
}
