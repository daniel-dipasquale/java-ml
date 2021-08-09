package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultRandomSupportContext;
import com.dipasquale.ai.rl.neat.switcher.factory.RandomSupportFactorySwitcher;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class RandomSupportSettings {
    private final RandomType nextIndex;
    private final RandomType isLessThan;

    private static ObjectSwitcher<RandomSupport> createSwitcher(final RandomType type, final ParallelismSupportSettings parallelism) {
        RandomType typeFixed = Optional.ofNullable(type)
                .orElse(RandomType.UNIFORM);

        return new RandomSupportFactorySwitcher(parallelism.isEnabled(), typeFixed);
    }

    ObjectSwitcher<RandomSupport> createNextIndex(final ParallelismSupportSettings parallelism) {
        return createSwitcher(nextIndex, parallelism);
    }

    ObjectSwitcher<RandomSupport> createIsLessThanSwitcher(final ParallelismSupportSettings parallelism) {
        return createSwitcher(isLessThan, parallelism);
    }

    DefaultRandomSupportContext create(final ParallelismSupportSettings parallelism) {
        ObjectSwitcher<RandomSupport> nextIndexSwitcher = createNextIndex(parallelism);
        ObjectSwitcher<RandomSupport> isLessThanSwitcher = createIsLessThanSwitcher(parallelism);

        return new DefaultRandomSupportContext(nextIndexSwitcher, isLessThanSwitcher);
    }
}
