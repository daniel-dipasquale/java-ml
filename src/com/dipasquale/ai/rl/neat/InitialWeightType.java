package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InitialWeightType {
    ALL_RANDOM,
    ONCE_RANDOM_REST_CARBON_COPY
}
