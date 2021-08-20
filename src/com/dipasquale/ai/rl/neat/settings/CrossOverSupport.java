package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.common.switcher.provider.IsLessThanRandomGateProviderSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber overrideConnectionExpressedRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useRandomParentConnectionWeightRate = FloatNumber.literal(0.6f);

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final float max, final ParallelismSupport parallelism) {
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectSwitcher.deconstruct(randomSupportSwitcher);

        return new IsLessThanRandomGateProviderSwitcher(parallelism.isEnabled(), randomSupportPair, max);
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final FloatNumber maximumNumber, final ParallelismSupport parallelism) {
        float max = maximumNumber.createFactorySwitcher(parallelism).getObject().create();

        return createIsLessThanProviderSwitcher(randomSupportSwitcher, max, parallelism);
    }

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelism, final RandomSupport random) {
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher = random.createIsLessThanSwitcher(parallelism);
        ObjectSwitcher<GateProvider> shouldOverrideConnectionExpressedSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, overrideConnectionExpressedRate, parallelism);
        ObjectSwitcher<GateProvider> shouldUseRandomParentConnectionWeightSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, useRandomParentConnectionWeightRate, parallelism);

        return new DefaultContextCrossOverSupport(shouldOverrideConnectionExpressedSwitcher, shouldUseRandomParentConnectionWeightSwitcher);
    }
}
