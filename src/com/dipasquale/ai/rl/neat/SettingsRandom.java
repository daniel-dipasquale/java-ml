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

    RandomSupportFloat getNextIndexRandomSupport(final SettingsParallelism parallelism) {
        return parallelism.getRandomSupport(nextIndex);
    }

    RandomSupportFloat getIsLessThanRandomSupport(final SettingsParallelism parallelism) {
        return parallelism.getRandomSupport(isLessThan);
    }

    ContextDefaultComponentFactory<ContextDefaultRandom> createFactory(final SettingsParallelism parallelism) {
        return context -> {
            RandomSupportFloat nextIndexRandomSupport = getNextIndexRandomSupport(parallelism);
            RandomSupportFloat isLessThanRandomSupport = getIsLessThanRandomSupport(parallelism);

            return new ContextDefaultRandom(nextIndexRandomSupport, isLessThanRandomSupport);
        };
    }
}
