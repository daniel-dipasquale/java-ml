package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SettingsConstants {
    static final DateTimeSupport DATE_TIME_SUPPORT_MILLISECONDS = DateTimeSupport.createMilliseconds();
    static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create();
    static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution();
    static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
    static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistributionConcurrent();

    static RandomSupportFloat getRandomSupport(final SettingsParallelism parallelism, final SettingsRandomType type) {
        if (!parallelism.isEnabled()) {
            return switch (type) {
                case UNIFORM -> SettingsConstants.RANDOM_SUPPORT_UNIFORM;

                case MEAN_DISTRIBUTED -> SettingsConstants.RANDOM_SUPPORT_MEAN_DISTRIBUTED;
            };
        }

        return switch (type) {
            case UNIFORM -> SettingsConstants.RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case MEAN_DISTRIBUTED -> SettingsConstants.RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT;
        };
    }
}
