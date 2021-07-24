package com.dipasquale.common.time;

import com.dipasquale.common.ObjectFactory;

import java.io.Serializable;

@FunctionalInterface
public interface ExpirationFactory extends ObjectFactory<ExpirationRecord> {
    @FunctionalInterface
    interface Creator extends Serializable {
        ExpirationFactory create(long expiryTime, long offset);
    }
}
