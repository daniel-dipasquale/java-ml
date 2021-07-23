package com.dipasquale.common.time;

import com.dipasquale.common.ArgumentValidatorSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ExpirySupportFactoryDefault implements ExpirySupport.Factory {
    @Serial
    private static final long serialVersionUID = -7185073988511695663L;
    private final DateTimeSupport dateTimeSupport;
    private final boolean rounded;

    @Override
    public ExpirySupport create(final long expiryTime, final long offset) {
        ArgumentValidatorSupport.ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(offset, "offset");
        ArgumentValidatorSupport.ensureGreaterThan(expiryTime, offset, "expiryTime");

        return new ExpirySupportDefault(dateTimeSupport, expiryTime, offset, rounded);
    }
}
