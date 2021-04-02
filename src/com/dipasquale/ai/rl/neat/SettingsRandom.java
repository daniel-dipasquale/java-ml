package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandom;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsRandom {
    @Builder.Default
    private final SettingsRandomType nextIndex = SettingsRandomType.UNIFORM;
    @Builder.Default
    private final SettingsRandomType isLessThan = SettingsRandomType.UNIFORM;

    private static RandomSupportFloat getRandomSupport(final SettingsParallelism parallelism, final SettingsRandomType type) {
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

    RandomSupportFloat getNextIndexRandomSupport(final SettingsParallelism parallelism) {
        return getRandomSupport(parallelism, nextIndex);
    }

    RandomSupportFloat getIsLessThanRandomSupport(final SettingsParallelism parallelism) {
        return getRandomSupport(parallelism, isLessThan);
    }

    ContextDefaultComponentFactory<ContextDefaultRandom> createFactory(final SettingsParallelism parallelism) {
        return context -> {
            RandomSupportFloat nextIndexRandomSupport = getNextIndexRandomSupport(parallelism);
            RandomSupportFloat isLessThanRandomSupport = getIsLessThanRandomSupport(parallelism);

            return new ContextDefaultRandom(nextIndexRandomSupport, isLessThanRandomSupport);
        };
    }
}
