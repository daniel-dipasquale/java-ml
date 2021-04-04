package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutation;
import com.dipasquale.common.FloatFactory;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsMutation {
    @Builder.Default
    private final SettingsFloatNumber addNodeMutationRate = SettingsFloatNumber.literal(0.1f);
    @Builder.Default
    private final SettingsFloatNumber addConnectionMutationRate = SettingsFloatNumber.literal(0.2f);
    @Builder.Default
    private final SettingsFloatNumber perturbConnectionsWeightRate = SettingsFloatNumber.literal(0.75f);
    @Builder.Default
    private final SettingsFloatNumber replaceConnectionsWeightRate = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber disableConnectionExpressedRate = SettingsFloatNumber.literal(0.05f);

    private static ContextDefaultMutation.Supplier createSupplier(final RandomSupportFloat randomSupport, final FloatFactory rateFactory) {
        float rate = rateFactory.create();

        return () -> randomSupport.isLessThan(rate);
    }

    private static ConnectionWeightSuppliers createConnectionWeightSuppliers(final RandomSupportFloat randomSupport, final FloatFactory perturbRateFactory, final FloatFactory replaceRateFactory) {
        float perturbRate = perturbRateFactory.create();
        float replaceRate = replaceRateFactory.create();
        float rate = (float) Math.ceil(perturbRate + replaceRate);

        if (Float.compare(rate, 0f) == 0) {
            return new ConnectionWeightSuppliers(() -> false, () -> false);
        }

        float perturbRateFixed = perturbRate / rate;
        float replaceRateFixed = replaceRate / rate;

        return new ConnectionWeightSuppliers(() -> randomSupport.isLessThan(perturbRateFixed), () -> randomSupport.isLessThan(replaceRateFixed));
    }

    ContextDefaultComponentFactory<ContextDefaultMutation> createFactory(final SettingsParallelism parallelism, final SettingsRandom random) {
        return context -> {
            RandomSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
            ContextDefaultMutation.Supplier shouldAddNodeMutation = createSupplier(randomSupport, addNodeMutationRate.createFactory(parallelism));
            ContextDefaultMutation.Supplier shouldAddConnectionMutation = createSupplier(randomSupport, addConnectionMutationRate.createFactory(parallelism));
            ConnectionWeightSuppliers connectionsWeight = createConnectionWeightSuppliers(randomSupport, perturbConnectionsWeightRate.createFactory(parallelism), replaceConnectionsWeightRate.createFactory(parallelism));
            ContextDefaultMutation.Supplier shouldDisableConnectionExpressed = createSupplier(randomSupport, disableConnectionExpressedRate.createFactory(parallelism));

            return new ContextDefaultMutation(shouldAddNodeMutation, shouldAddConnectionMutation, connectionsWeight.perturb, connectionsWeight.replace, shouldDisableConnectionExpressed);
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ConnectionWeightSuppliers {
        private final ContextDefaultMutation.Supplier perturb;
        private final ContextDefaultMutation.Supplier replace;
    }
}
