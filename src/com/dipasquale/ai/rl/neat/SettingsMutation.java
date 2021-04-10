package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutationSupport;
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

    private static GateProvider createSupplier(final RandomSupportFloat randomSupport, final FloatFactory rateFactory) {
        float rate = rateFactory.create();

        return () -> randomSupport.isLessThan(rate);
    }

    private static ConnectionWeightGateProvider createConnectionWeightSuppliers(final RandomSupportFloat randomSupport, final FloatFactory perturbRateFactory, final FloatFactory replaceRateFactory) {
        float perturbRate = perturbRateFactory.create();
        float replaceRate = replaceRateFactory.create();
        float rate = (float) Math.ceil(perturbRate + replaceRate);

        if (Float.compare(rate, 0f) == 0) {
            return new ConnectionWeightGateProvider(() -> false, () -> false);
        }

        float perturbRateFixed = perturbRate / rate;
        float replaceRateFixed = replaceRate / rate;

        return new ConnectionWeightGateProvider(() -> randomSupport.isLessThan(perturbRateFixed), () -> randomSupport.isLessThan(replaceRateFixed));
    }

    ContextDefaultMutationSupport create(final SettingsParallelism parallelism, final SettingsRandom random) {
        RandomSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
        GateProvider shouldAddNodeMutation = createSupplier(randomSupport, addNodeMutationRate.createFactory(parallelism));
        GateProvider shouldAddConnectionMutation = createSupplier(randomSupport, addConnectionMutationRate.createFactory(parallelism));
        ConnectionWeightGateProvider connectionsWeight = createConnectionWeightSuppliers(randomSupport, perturbConnectionsWeightRate.createFactory(parallelism), replaceConnectionsWeightRate.createFactory(parallelism));
        GateProvider shouldDisableConnectionExpressed = createSupplier(randomSupport, disableConnectionExpressedRate.createFactory(parallelism));

        return new ContextDefaultMutationSupport(shouldAddNodeMutation, shouldAddConnectionMutation, connectionsWeight.perturb, connectionsWeight.replace, shouldDisableConnectionExpressed);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ConnectionWeightGateProvider {
        private final GateProvider perturb;
        private final GateProvider replace;
    }
}
