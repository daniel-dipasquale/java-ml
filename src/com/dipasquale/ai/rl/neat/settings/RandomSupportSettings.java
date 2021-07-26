package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultRandomSupportContext;
import com.dipasquale.ai.rl.neat.switcher.RandomSupportSwitcher;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomSupportSettings {
    private final RandomType nextIndex;
    private final RandomType isLessThan;

    private static ObjectSwitcher<RandomSupport> createSwitcher(final RandomType type, final ParallelismSupportSettings parallelism) {
        RandomType typeFixed = Optional.ofNullable(type)
                .orElse(RandomType.UNIFORM);

        return new RandomSupportSwitcher(parallelism.isEnabled(), typeFixed);
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
