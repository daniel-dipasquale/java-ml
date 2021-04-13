package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultRandomSupport;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsRandomSupport {
    @Builder.Default
    private final SettingsRandomType nextIndex = SettingsRandomType.UNIFORM;
    @Builder.Default
    private final SettingsRandomType isLessThan = SettingsRandomType.UNIFORM;

    RandomSupportFloat getNextIndexSupport(final SettingsParallelismSupport parallelism) {
        return parallelism.getRandomSupport(nextIndex);
    }

    RandomSupportFloat getIsLessThanSupport(final SettingsParallelismSupport parallelism) {
        return parallelism.getRandomSupport(isLessThan);
    }

    ContextDefaultRandomSupport create(final SettingsParallelismSupport parallelism) {
        RandomSupportFloat nextIndex = getNextIndexSupport(parallelism);
        RandomSupportFloat isLessThan = getIsLessThanSupport(parallelism);

        return new ContextDefaultRandomSupport(nextIndex, isLessThan);
    }
}
