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

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createIntegerRandomSupportProfile(final ParallelismSupport parallelismSupport) {
        return new RandomSupportFactoryProfile(parallelismSupport.isEnabled(), integerGenerator);
    }

    public ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> createFloatRandomSupportProfile(final ParallelismSupport parallelismSupport) {
        return new RandomSupportFactoryProfile(parallelismSupport.isEnabled(), floatGenerator);
    }

    DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> integerRandomSupportProfile = createIntegerRandomSupportProfile(parallelismSupport);
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> floatRandomSupportProfile = createFloatRandomSupportProfile(parallelismSupport);

        return new DefaultContextRandomSupport(integerRandomSupportProfile, floatRandomSupportProfile);
    }
}
