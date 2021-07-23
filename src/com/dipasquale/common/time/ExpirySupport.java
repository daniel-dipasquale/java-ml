package com.dipasquale.common.time;

import com.dipasquale.common.ArgumentValidatorSupport;

import java.io.Serializable;

@FunctionalInterface
public interface ExpirySupport extends Serializable {
    ExpiryRecord next();

    private static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset, final boolean rounded) {
        ArgumentValidatorSupport.ensureNotNull(dateTimeSupport, "dateTimeSupport");
        ArgumentValidatorSupport.ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(offset, "offset");
        ArgumentValidatorSupport.ensureGreaterThan(expiryTime, offset, "expiryTime");

        return new ExpirySupportDefault(dateTimeSupport, expiryTime, offset, rounded);
    }

    static ExpirySupport createRounded(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, true);
    }

    static ExpirySupport createRounded(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return create(dateTimeSupport, expiryTime, 0L, true);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, false);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return create(dateTimeSupport, expiryTime, 0L, false);
    }

    static Factory createFactory(final DateTimeSupport dateTimeSupport, final boolean rounded) {
        return new ExpirySupportFactoryDefault(dateTimeSupport, rounded);
    }

    static Factory createRoundedFactory(final DateTimeSupport dateTimeSupport) {
        ArgumentValidatorSupport.ensureNotNull(dateTimeSupport, "dateTimeSupport");

        return createFactory(dateTimeSupport, true);
    }

    static Factory createFactory(final DateTimeSupport dateTimeSupport) {
        ArgumentValidatorSupport.ensureNotNull(dateTimeSupport, "dateTimeSupport");

        return createFactory(dateTimeSupport, false);
    }

    @FunctionalInterface
    interface Factory extends Serializable {
        ExpirySupport create(long expiryTime, long offset);

        default ExpirySupport create(final long expiryTime) {
            return create(expiryTime, 0L);
        }
    }
}
