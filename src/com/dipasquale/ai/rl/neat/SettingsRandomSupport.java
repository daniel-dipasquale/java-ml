package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultRandomSupport;
import com.dipasquale.concurrent.RandomBiSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsRandomSupport {
    private final RandomBiSupportFloat nextIndexSupport;
    private final RandomBiSupportFloat isLessThanSupport;

    @Builder
    private static SettingsRandomSupport create(final SettingsRandomType nextIndex, final SettingsRandomType isLessThan) {
        SettingsRandomType nextIndexFixed = Optional.ofNullable(nextIndex)
                .orElse(SettingsRandomType.UNIFORM);

        SettingsRandomType isLessThanFixed = Optional.ofNullable(nextIndex)
                .orElse(SettingsRandomType.UNIFORM);

        RandomBiSupportFloat nextIndexSupport = new SettingsRandomBiSupport(nextIndexFixed);
        RandomBiSupportFloat isLessThanSupport = new SettingsRandomBiSupport(isLessThanFixed);

        return new SettingsRandomSupport(nextIndexSupport, isLessThanSupport);
    }

    RandomBiSupportFloat getNextIndexSupport(final SettingsParallelismSupport parallelism) {
        return nextIndexSupport.selectContended(parallelism.isEnabled());
    }

    RandomBiSupportFloat getIsLessThanSupport(final SettingsParallelismSupport parallelism) {
        return isLessThanSupport.selectContended(parallelism.isEnabled());
    }

    ContextDefaultRandomSupport create(final SettingsParallelismSupport parallelism) {
        RandomBiSupportFloat nextIndex = getNextIndexSupport(parallelism);
        RandomBiSupportFloat isLessThan = getIsLessThanSupport(parallelism);

        return new ContextDefaultRandomSupport(nextIndex, isLessThan);
    }
}
