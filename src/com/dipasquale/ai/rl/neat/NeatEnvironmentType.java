package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NeatEnvironmentType {
    ISOLATED,
    SHARED;

    static NeatEnvironmentType from(final NeatEnvironment neatEnvironment) {
        if (neatEnvironment instanceof IsolatedNeatEnvironment) {
            return NeatEnvironmentType.ISOLATED;
        }

        if (neatEnvironment instanceof SharedNeatEnvironment) {
            return NeatEnvironmentType.SHARED;
        }

        String message = String.format("The neatEnvironment provided is not supported: %s", neatEnvironment);

        throw new IllegalArgumentException(message);
    }
}
