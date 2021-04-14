package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOverSupport;
import com.dipasquale.concurrent.FloatBiFactory;
import com.dipasquale.common.RandomSupportFloat;
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

    private static CrossOverGateProvider createCrossOverSuppliers(final RandomSupportFloat randomSupport, final FloatBiFactory mateOnlyRateFactory, final FloatBiFactory mutateOnlyRateFactory) {
        float mateOnlyRate = mateOnlyRateFactory.create();
        float mutateOnlyRate = mutateOnlyRateFactory.create();
        float rate = (float) Math.ceil(mateOnlyRate + mutateOnlyRate);

        if (Float.compare(rate, 0f) == 0) {
            return new CrossOverGateProvider(() -> true, () -> false, () -> false);
        }

        float mateAndMutateRate = (mateOnlyRate + mutateOnlyRate) / rate;
        float mateOnlyRateFixed = mateOnlyRate / rate;
        float mutateOnlyRateFixed = mutateOnlyRate / rate;

        return new CrossOverGateProvider(() -> randomSupport.isLessThan(mateAndMutateRate), () -> randomSupport.isLessThan(mateOnlyRateFixed), () -> randomSupport.isLessThan(mutateOnlyRateFixed));
    }

    private static GateProvider createSupplier(final RandomSupportFloat randomSupport, final FloatBiFactory rateFactory) {
        float rate = rateFactory.create();

        return () -> randomSupport.isLessThan(rate);
    }

    ContextDefaultCrossOverSupport create(final SettingsParallelismSupport parallelism, final SettingsRandomSupport random) {
        RandomSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
        CrossOverGateProvider crossOver = createCrossOverSuppliers(randomSupport, mateOnlyRate.createFactory(parallelism), mutateOnlyRate.createFactory(parallelism));
        GateProvider shouldOverrideConnectionExpressed = createSupplier(randomSupport, overrideConnectionExpressedRate.createFactory(parallelism));
        GateProvider shouldUseRandomParentConnectionWeight = createSupplier(randomSupport, useRandomParentConnectionWeightRate.createFactory(parallelism));

        return new ContextDefaultCrossOverSupport(crossOver.mateAndMutate, crossOver.mateOnly, crossOver.mutateOnly, shouldOverrideConnectionExpressed, shouldUseRandomParentConnectionWeight);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class CrossOverGateProvider {
        private final GateProvider mateAndMutate;
        private final GateProvider mateOnly;
        private final GateProvider mutateOnly;
    }
}
