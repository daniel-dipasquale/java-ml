package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MetricCollectionType {
    ENABLED,
    SKIP_NORMAL_DISTRIBUTION_METRICS,
    ONLY_KEEP_LAST_FITNESS_EVALUATION,
    ONLY_KEEP_LAST_GENERATION,
    ONLY_KEEP_LAST_ITERATION
}
