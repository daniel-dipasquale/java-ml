package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class ContextObjectMetricsParameters implements Context.MetricsParameters, Serializable {
    @Serial
    private static final long serialVersionUID = 3933676175004672652L;
    private final boolean enabled;

    @Override
    public boolean enabled() {
        return enabled;
    }
}
