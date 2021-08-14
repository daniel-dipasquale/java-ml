package com.dipasquale.common.time;

import com.dipasquale.common.factory.ObjectFactory;

@FunctionalInterface
public interface ExpirationFactory extends ObjectFactory<ExpirationRecord> {
    @FunctionalInterface
    interface Creator {
        ExpirationFactory create(long expirationTime, long offset);
    }
}
