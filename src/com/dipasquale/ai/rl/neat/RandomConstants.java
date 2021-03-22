package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RandomConstants {
    public static final RandomSupportFloat UNIFORM = RandomSupportFloat.create();
    public static final RandomSupportFloat UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
    public static final RandomSupportFloat MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution();
    public static final RandomSupportFloat MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistributionConcurrent();
}
