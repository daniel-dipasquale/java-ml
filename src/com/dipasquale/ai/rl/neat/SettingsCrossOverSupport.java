package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.GateBiProvider;
import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOverSupport;
import com.dipasquale.concurrent.FloatBiFactory;
import com.dipasquale.concurrent.random.RandomBiSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsCrossOverSupport {
    @Builder.Default
    private final SettingsFloatNumber mateOnlyRate = SettingsFloatNumber.literal(0.2f);
    @Builder.Default
    private final SettingsFloatNumber mutateOnlyRate = SettingsFloatNumber.literal(0.25f);
    @Builder.Default
    private final SettingsFloatNumber overrideConnectionExpressedRate = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber useRandomParentConnectionWeightRate = SettingsFloatNumber.literal(0.6f);

    private static CrossOverGateProvider createCrossOverProviders(final RandomBiSupportFloat randomSupport, final FloatBiFactory mateOnlyRateFactory, final FloatBiFactory mutateOnlyRateFactory) {
        float mateOnlyRate = mateOnlyRateFactory.create();
        float mutateOnlyRate = mutateOnlyRateFactory.create();
        float rate = (float) Math.ceil(mateOnlyRate + mutateOnlyRate);

        if (Float.compare(rate, 0f) == 0) {
            GateBiProvider mateAndMutate = GateBiProvider.createLiteral(true);
            GateBiProvider mateOnlyOrMutateOnly = GateBiProvider.createLiteral(false);

            return new CrossOverGateProvider(mateAndMutate, mateOnlyOrMutateOnly, mateOnlyOrMutateOnly);
        }

        float mateAndMutateRate = (mateOnlyRate + mutateOnlyRate) / rate;
        GateBiProvider mateAndMutate = GateBiProvider.createIsLessThan(randomSupport, mateAndMutateRate);
        float mateOnlyRateFixed = mateOnlyRate / rate;
        GateBiProvider mateOnly = GateBiProvider.createIsLessThan(randomSupport, mateOnlyRateFixed);
        float mutateOnlyRateFixed = mutateOnlyRate / rate;
        GateBiProvider mutateOnly = GateBiProvider.createIsLessThan(randomSupport, mutateOnlyRateFixed);

        return new CrossOverGateProvider(mateAndMutate, mateOnly, mutateOnly);
    }

    private static GateProvider createProvider(final RandomBiSupportFloat randomSupport, final FloatBiFactory rateFactory) {
        float rate = rateFactory.create();

        return GateBiProvider.createIsLessThan(randomSupport, rate);
    }

    ContextDefaultCrossOverSupport create(final SettingsParallelismSupport parallelism, final SettingsRandomSupport random) {
        RandomBiSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
        CrossOverGateProvider crossOver = createCrossOverProviders(randomSupport, mateOnlyRate.createFactory(parallelism), mutateOnlyRate.createFactory(parallelism));
        GateProvider shouldOverrideConnectionExpressed = createProvider(randomSupport, overrideConnectionExpressedRate.createFactory(parallelism));
        GateProvider shouldUseRandomParentConnectionWeight = createProvider(randomSupport, useRandomParentConnectionWeightRate.createFactory(parallelism));

        return new ContextDefaultCrossOverSupport(crossOver.mateAndMutate, crossOver.mateOnly, crossOver.mutateOnly, shouldOverrideConnectionExpressed, shouldUseRandomParentConnectionWeight);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class CrossOverGateProvider {
        private final GateProvider mateAndMutate;
        private final GateProvider mateOnly;
        private final GateProvider mutateOnly;
    }
}
