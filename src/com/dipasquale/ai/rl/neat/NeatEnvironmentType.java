package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NeatEnvironmentType {
    SECLUDED,
    COMMUNAL;

    static NeatEnvironmentType from(final NeatEnvironment environment) {
        if (environment instanceof SecludedNeatEnvironment) {
            return NeatEnvironmentType.SECLUDED;
        }

        if (environment instanceof CommunalNeatEnvironment) {
            return NeatEnvironmentType.COMMUNAL;
        }

        String message = String.format("The environment provided is not supported: %s", environment);

        throw new IllegalArgumentException(message);
    }
}
