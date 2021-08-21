package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import com.dipasquale.ai.rl.neat.profile.factory.RandomSupportFactoryProfile;
import com.dipasquale.common.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class RandomSupport {
    private final RandomType nextIndex;
    private final RandomType isLessThan;

    private static ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createProfile(final ParallelismSupport parallelism, final RandomType type) {
        RandomType typeFixed = Optional.ofNullable(type)
                .orElse(RandomType.UNIFORM);

        return new RandomSupportFactoryProfile(parallelism.isEnabled(), typeFixed);
    }

    ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createNextIndexProfile(final ParallelismSupport parallelism) {
        return createProfile(parallelism, nextIndex);
    }

    ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createIsLessThanProfile(final ParallelismSupport parallelism) {
        return createProfile(parallelism, isLessThan);
    }

    DefaultContextRandomSupport create(final ParallelismSupport parallelism) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> nextIndexProfile = createNextIndexProfile(parallelism);
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> isLessThanProfile = createIsLessThanProfile(parallelism);

        return new DefaultContextRandomSupport(nextIndexProfile, isLessThanProfile);
    }
}
