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
    private final RandomType integerGenerator = RandomType.UNIFORM;
    @Builder.Default
    private final RandomType floatGenerator = RandomType.UNIFORM;

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createIntegerRandomSupportProfile(final ParallelismSupport parallelism) {
        return new RandomSupportFactoryProfile(parallelism.isEnabled(), integerGenerator);
    }

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createFloatRandomSupportProfile(final ParallelismSupport parallelism) {
        return new RandomSupportFactoryProfile(parallelism.isEnabled(), floatGenerator);
    }

    DefaultContextRandomSupport create(final ParallelismSupport parallelism) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> integerRandomSupportProfile = createIntegerRandomSupportProfile(parallelism);
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> floatRandomSupportProfile = createFloatRandomSupportProfile(parallelism);

        return new DefaultContextRandomSupport(integerRandomSupportProfile, floatRandomSupportProfile);
    }
}
