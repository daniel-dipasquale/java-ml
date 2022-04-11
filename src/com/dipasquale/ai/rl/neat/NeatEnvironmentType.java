package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NeatEnvironmentType {
    CONFINED,
    SHARED;

    static NeatEnvironmentType from(final NeatEnvironment environment) {
        if (environment instanceof ConfinedNeatEnvironment) {
            return NeatEnvironmentType.CONFINED;
        }

        if (environment instanceof SharedNeatEnvironment) {
            return NeatEnvironmentType.SHARED;
        }

        String message = String.format("The environment provided is not supported: %s", environment);

        throw new IllegalArgumentException(message);
    }
}
