package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SettingsConstants {
    public static final DateTimeSupport DATE_TIME_SUPPORT_MILLISECONDS = DateTimeSupport.createMilliseconds();
    public static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create();
    public static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution();
    public static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
    public static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistributionConcurrent();
}
