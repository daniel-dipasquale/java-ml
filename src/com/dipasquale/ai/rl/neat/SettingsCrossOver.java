package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOver;
import com.dipasquale.ai.rl.neat.genotype.GenomeCrossOver;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsCrossOver {
    @Builder.Default
    private final SettingsFloatNumber mateOnlyRate = SettingsFloatNumber.literal(0.2f);
    @Builder.Default
    private final SettingsFloatNumber mutateOnlyRate = SettingsFloatNumber.literal(0.25f);
    @Builder.Default
    private final SettingsFloatNumber overrideConnectionExpressedRate = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber useRandomParentConnectionWeightRate = SettingsFloatNumber.literal(0.6f);

    private static CrossOverSuppliers createCrossOverSuppliers(final RandomSupportFloat randomSupport, final FloatFactory mateOnlyRateFactory, final FloatFactory mutateOnlyRateFactory) {
        float mateOnlyRate = mateOnlyRateFactory.create();
        float mutateOnlyRate = mutateOnlyRateFactory.create();
        float rate = (float) Math.ceil(mateOnlyRate + mutateOnlyRate);

        if (Float.compare(rate, 0f) == 0) {
            return new CrossOverSuppliers(() -> true, () -> false, () -> false);
        }

        float mateAndMutateRate = (mateOnlyRate + mutateOnlyRate) / rate;
        float mateOnlyRateFixed = mateOnlyRate / rate;
        float mutateOnlyRateFixed = mutateOnlyRate / rate;

        return new CrossOverSuppliers(() -> randomSupport.isLessThan(mateAndMutateRate), () -> randomSupport.isLessThan(mateOnlyRateFixed), () -> randomSupport.isLessThan(mutateOnlyRateFixed));
    }

    private static ContextDefaultCrossOver.Supplier createSupplier(final RandomSupportFloat randomSupport, final FloatFactory rateFactory) {
        float rate = rateFactory.create();

        return () -> randomSupport.isLessThan(rate);
    }

    ContextDefaultComponentFactory<ContextDefaultCrossOver> createFactory(final SettingsParallelism parallelism, final SettingsRandom random) {
        return context -> {
            RandomSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
            CrossOverSuppliers crossOver = createCrossOverSuppliers(randomSupport, mateOnlyRate.createFactory(parallelism), mutateOnlyRate.createFactory(parallelism));
            ContextDefaultCrossOver.Supplier shouldOverrideConnectionExpressed = createSupplier(randomSupport, overrideConnectionExpressedRate.createFactory(parallelism));
            ContextDefaultCrossOver.Supplier shouldUseRandomParentConnectionWeight = createSupplier(randomSupport, useRandomParentConnectionWeightRate.createFactory(parallelism));

            return new ContextDefaultCrossOver(crossOver.mateAndMutate, crossOver.mateOnly, crossOver.mutateOnly, shouldOverrideConnectionExpressed, shouldUseRandomParentConnectionWeight, new GenomeCrossOver(context));
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class CrossOverSuppliers {
        private final ContextDefaultCrossOver.Supplier mateAndMutate;
        private final ContextDefaultCrossOver.Supplier mateOnly;
        private final ContextDefaultCrossOver.Supplier mutateOnly;
    }
}
