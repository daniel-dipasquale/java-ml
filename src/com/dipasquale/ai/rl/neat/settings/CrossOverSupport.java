package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.dual.profile.provider.IsLessThanRandomGateProviderProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber overrideExpressedRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useRandomParentWeightRate = FloatNumber.literal(0.6f);

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelism, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelism.isEnabled(), randomSupportPair, max);
    }

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelism, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.createFactoryProfile(parallelism).getObject().create();

        return createIsLessThanProviderProfile(parallelism, randomSupportPair, max);
    }

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelism, final RandomSupport random) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> randomSupportProfile = random.createIsLessThanProfile(parallelism);
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldOverrideExpressedProfile = createIsLessThanProviderProfile(parallelism, randomSupportPair, overrideExpressedRate);
        ObjectProfile<GateProvider> shouldUseRandomParentWeightProfile = createIsLessThanProviderProfile(parallelism, randomSupportPair, useRandomParentWeightRate);

        return new DefaultContextCrossOverSupport(shouldOverrideExpressedProfile, shouldUseRandomParentWeightProfile);
    }
}
