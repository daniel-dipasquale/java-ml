package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.GateBiProvider;
import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutationSupport;
import com.dipasquale.concurrent.FloatBiFactory;
import com.dipasquale.concurrent.RandomBiSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsMutationSupport {
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

    private static GateProvider createProvider(final RandomBiSupportFloat randomSupport, final FloatBiFactory rateFactory) {
        float rate = rateFactory.create();

        return GateBiProvider.createIsLessThan(randomSupport, rate);
    }

    private static ConnectionWeightGateProvider createConnectionWeightProviders(final RandomBiSupportFloat randomSupport, final FloatBiFactory perturbRateFactory, final FloatBiFactory replaceRateFactory) {
        float perturbRate = perturbRateFactory.create();
        float replaceRate = replaceRateFactory.create();
        float rate = (float) Math.ceil(perturbRate + replaceRate);

        if (Float.compare(rate, 0f) == 0) {
            GateBiProvider perturbOrReplace = GateBiProvider.createLiteral(false);

            return new ConnectionWeightGateProvider(perturbOrReplace, perturbOrReplace);
        }

        float perturbRateFixed = perturbRate / rate;
        GateBiProvider perturb = GateBiProvider.createIsLessThan(randomSupport, perturbRateFixed);
        float replaceRateFixed = replaceRate / rate;
        GateBiProvider replace = GateBiProvider.createIsLessThan(randomSupport, replaceRateFixed);

        return new ConnectionWeightGateProvider(perturb, replace);
    }

    ContextDefaultMutationSupport create(final SettingsParallelismSupport parallelism, final SettingsRandomSupport random) {
        RandomBiSupportFloat randomSupport = random.getIsLessThanSupport(parallelism);
        GateProvider shouldAddNodeMutation = createProvider(randomSupport, addNodeMutationRate.createFactory(parallelism));
        GateProvider shouldAddConnectionMutation = createProvider(randomSupport, addConnectionMutationRate.createFactory(parallelism));
        ConnectionWeightGateProvider connectionsWeight = createConnectionWeightProviders(randomSupport, perturbConnectionsWeightRate.createFactory(parallelism), replaceConnectionsWeightRate.createFactory(parallelism));
        GateProvider shouldDisableConnectionExpressed = createProvider(randomSupport, disableConnectionExpressedRate.createFactory(parallelism));

        return new ContextDefaultMutationSupport(shouldAddNodeMutation, shouldAddConnectionMutation, connectionsWeight.perturb, connectionsWeight.replace, shouldDisableConnectionExpressed);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ConnectionWeightGateProvider {
        private final GateProvider perturb;
        private final GateProvider replace;
    }
}
