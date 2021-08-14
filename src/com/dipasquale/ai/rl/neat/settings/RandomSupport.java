package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultRandomSupportContext;
import com.dipasquale.ai.rl.neat.switcher.factory.RandomSupportFactorySwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class RandomSupport {
    private final RandomType nextIndex;
    private final RandomType isLessThan;

    private static ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> createSwitcher(final RandomType type, final ParallelismSupport parallelism) {
        RandomType typeFixed = Optional.ofNullable(type)
                .orElse(RandomType.UNIFORM);

        return new RandomSupportFactorySwitcher(parallelism.isEnabled(), typeFixed);
    }

    ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> createNextIndexSwitcher(final ParallelismSupport parallelism) {
        return createSwitcher(nextIndex, parallelism);
    }

    ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> createIsLessThanSwitcher(final ParallelismSupport parallelism) {
        return createSwitcher(isLessThan, parallelism);
    }

    DefaultRandomSupportContext create(final ParallelismSupport parallelism) {
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> nextIndexSwitcher = createNextIndexSwitcher(parallelism);
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> isLessThanSwitcher = createIsLessThanSwitcher(parallelism);

        return new DefaultRandomSupportContext(nextIndexSwitcher, isLessThanSwitcher);
    }
}
