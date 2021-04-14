package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GateBiProviderLiteral implements GateBiProvider {
    @Serial
    private static final long serialVersionUID = -4019029192057751329L;
    private final boolean value;

    @Override
    public boolean isOn() {
        return value;
    }

    @Override
    public GateBiProvider selectContended(final boolean contended) {
        return this;
    }
}
