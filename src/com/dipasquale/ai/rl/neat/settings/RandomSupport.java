package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.profile.factory.RandomSupportFactoryProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class RandomSupport {
    @Builder.Default
    private final RandomType nextIndex = RandomType.UNIFORM;
    @Builder.Default
    private final RandomType isLessThan = RandomType.UNIFORM;

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createNextIndexProfile(final ParallelismSupport parallelism) {
        return new RandomSupportFactoryProfile(parallelism.isEnabled(), nextIndex);
    }

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createIsLessThanProfile(final ParallelismSupport parallelism) {
        return new RandomSupportFactoryProfile(parallelism.isEnabled(), isLessThan);
    }

    DefaultContextRandomSupport create(final ParallelismSupport parallelism) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> nextIndexProfile = createNextIndexProfile(parallelism);
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> isLessThanProfile = createIsLessThanProfile(parallelism);

        return new DefaultContextRandomSupport(nextIndexProfile, isLessThanProfile);
    }
}
