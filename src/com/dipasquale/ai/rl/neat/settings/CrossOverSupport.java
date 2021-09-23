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
    private final FloatNumber overrideExpressedConnectionRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useWeightFromRandomParentRate = FloatNumber.literal(0.6f);

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final float max) {
        return new IsLessThanRandomGateProviderProfile(parallelismSupport.isEnabled(), randomSupportPair, max);
    }

    private static ObjectProfile<GateProvider> createIsLessThanProviderProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final FloatNumber maximumNumber) {
        float max = maximumNumber.createFactoryProfile(parallelismSupport).getObject().create();

        return createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, max);
    }

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelismSupport, final RandomSupport randomSupport) {
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> randomSupportProfile = randomSupport.createFloatRandomSupportProfile(parallelismSupport);
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        ObjectProfile<GateProvider> shouldOverrideExpressedConnectionProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, overrideExpressedConnectionRate);
        ObjectProfile<GateProvider> shouldUseWeightFromRandomParentProfile = createIsLessThanProviderProfile(parallelismSupport, randomSupportPair, useWeightFromRandomParentRate);

        return new DefaultContextCrossOverSupport(shouldOverrideExpressedConnectionProfile, shouldUseWeightFromRandomParentProfile);
    }
}
