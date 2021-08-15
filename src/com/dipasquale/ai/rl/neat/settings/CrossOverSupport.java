package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.provider.LiteralGateProvider;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.common.switcher.provider.IsLessThanRandomGateProviderSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber mateOnlyRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber mutateOnlyRate = FloatNumber.literal(0.25f);
    @Builder.Default
    private final FloatNumber overrideConnectionExpressedRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useRandomParentConnectionWeightRate = FloatNumber.literal(0.6f);

    private static ObjectSwitcher<GateProvider> createLiteralProviderSwitcher(final boolean isOn, final ParallelismSupport parallelism) {
        return new DefaultObjectSwitcher<>(parallelism.isEnabled(), new LiteralGateProvider(isOn));
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final float max, final ParallelismSupport parallelism) {
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectSwitcher.deconstruct(randomSupportSwitcher);

        return new IsLessThanRandomGateProviderSwitcher(parallelism.isEnabled(), randomSupportPair, max);
    }

    private CrossOverProviderSwitchers createCrossOverProviderSwitchers(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final ParallelismSupport parallelism) {
        float _mateOnlyRate = mateOnlyRate.createFactorySwitcher(parallelism).getObject().create();
        float _mutateOnlyRate = mutateOnlyRate.createFactorySwitcher(parallelism).getObject().create();
        float totalRate = (float) Math.ceil(_mateOnlyRate + _mutateOnlyRate);

        if (Float.compare(totalRate, 0f) == 0) {
            ObjectSwitcher<GateProvider> mateAndMutateSwitcher = createLiteralProviderSwitcher(true, parallelism);
            ObjectSwitcher<GateProvider> mateOnlyOrMutateOnlySwitcher = createLiteralProviderSwitcher(false, parallelism);

            return new CrossOverProviderSwitchers(mateAndMutateSwitcher, mateOnlyOrMutateOnlySwitcher, mateOnlyOrMutateOnlySwitcher);
        }

        float _mateAndMutateFixed = (_mateOnlyRate + _mutateOnlyRate) / totalRate;
        ObjectSwitcher<GateProvider> mateAndMutateSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, _mateAndMutateFixed, parallelism);
        float _mateOnlyRateFixed = _mateOnlyRate / totalRate;
        ObjectSwitcher<GateProvider> mateOnlySwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, _mateOnlyRateFixed, parallelism);
        float _mutateOnlyRateFixed = _mutateOnlyRate / totalRate;
        ObjectSwitcher<GateProvider> mutateOnlySwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, _mutateOnlyRateFixed, parallelism);

        return new CrossOverProviderSwitchers(mateAndMutateSwitcher, mateOnlySwitcher, mutateOnlySwitcher);
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final FloatNumber maximumNumber, final ParallelismSupport parallelism) {
        float max = maximumNumber.createFactorySwitcher(parallelism).getObject().create();

        return createIsLessThanProviderSwitcher(randomSupportSwitcher, max, parallelism);
    }

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelism, final RandomSupport random) {
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher = random.createIsLessThanSwitcher(parallelism);
        CrossOverProviderSwitchers crossOverProviderSwitchers = createCrossOverProviderSwitchers(randomSupportSwitcher, parallelism);
        ObjectSwitcher<GateProvider> shouldOverrideConnectionExpressedSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, overrideConnectionExpressedRate, parallelism);
        ObjectSwitcher<GateProvider> shouldUseRandomParentConnectionWeightSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, useRandomParentConnectionWeightRate, parallelism);

        return new DefaultContextCrossOverSupport(crossOverProviderSwitchers.mateAndMutate, crossOverProviderSwitchers.mateOnly, crossOverProviderSwitchers.mutateOnly, shouldOverrideConnectionExpressedSwitcher, shouldUseRandomParentConnectionWeightSwitcher);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class CrossOverProviderSwitchers {
        private final ObjectSwitcher<GateProvider> mateAndMutate;
        private final ObjectSwitcher<GateProvider> mateOnly;
        private final ObjectSwitcher<GateProvider> mutateOnly;
    }
}
