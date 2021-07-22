package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ExpirySupportFactoryDefault implements ExpirySupport.Factory {
    @Serial
    private static final long serialVersionUID = -7185073988511695663L;
    private final DateTimeSupport dateTimeSupport;
    private final boolean slider;

    @Override
    public ExpirySupport create(final long expiryTime, final long offset) {
        return new ExpirySupportDefault(dateTimeSupport, expiryTime, offset, slider);
    }
}
