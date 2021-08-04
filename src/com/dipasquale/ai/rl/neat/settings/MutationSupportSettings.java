package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultMutationSupportContext;
import com.dipasquale.common.Pair;
import com.dipasquale.common.provider.GateProvider;
import com.dipasquale.common.provider.LiteralGateProvider;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.switcher.DefaultObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.common.switcher.provider.IsLessThanRandomGateProviderSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class MutationSupportSettings {
    @Builder.Default
    private final FloatNumberSettings addNodeMutationRate = FloatNumberSettings.literal(0.1f);
    @Builder.Default
    private final FloatNumberSettings addConnectionMutationRate = FloatNumberSettings.literal(0.2f);
    @Builder.Default
    private final FloatNumberSettings perturbConnectionsWeightRate = FloatNumberSettings.literal(0.75f);
    @Builder.Default
    private final FloatNumberSettings replaceConnectionsWeightRate = FloatNumberSettings.literal(0.5f);
    @Builder.Default
    private final FloatNumberSettings disableConnectionExpressedRate = FloatNumberSettings.literal(0.05f);

    private static ObjectSwitcher<GateProvider> createLiteralProviderSwitcher(final boolean isOn, final ParallelismSupportSettings parallelism) {
        return new DefaultObjectSwitcher<>(parallelism.isEnabled(), new LiteralGateProvider(isOn));
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<RandomSupport> randomSupportSwitcher, final float max, final ParallelismSupportSettings parallelism) {
        Pair<RandomSupport> randomSupportPair = ObjectSwitcher.deconstruct(randomSupportSwitcher);

        return new IsLessThanRandomGateProviderSwitcher(parallelism.isEnabled(), randomSupportPair, max);
    }

    private ConnectionWeightProviderSwitchers createConnectionWeightProviderSwitchers(final ObjectSwitcher<RandomSupport> randomSupportSwitcher, final ParallelismSupportSettings parallelism) {
        float perturbRate = perturbConnectionsWeightRate.createFactorySwitcher(parallelism).getObject().create();
        float replaceRate = replaceConnectionsWeightRate.createFactorySwitcher(parallelism).getObject().create();
        float totalRate = (float) Math.ceil(perturbRate + replaceRate);

        if (Float.compare(totalRate, 0f) == 0) {
            ObjectSwitcher<GateProvider> perturbOrReplaceSwitcher = createLiteralProviderSwitcher(false, parallelism);

            return new ConnectionWeightProviderSwitchers(perturbOrReplaceSwitcher, perturbOrReplaceSwitcher);
        }

        float perturbRateFixed = perturbRate / totalRate;
        ObjectSwitcher<GateProvider> perturbSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, perturbRateFixed, parallelism);
        float replaceRateFixed = replaceRate / totalRate;
        ObjectSwitcher<GateProvider> replaceSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, replaceRateFixed, parallelism);

        return new ConnectionWeightProviderSwitchers(perturbSwitcher, replaceSwitcher);
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<RandomSupport> randomSupportSwitcher, final FloatNumberSettings maximumNumber, final ParallelismSupportSettings parallelism) {
        float max = maximumNumber.createFactorySwitcher(parallelism).getObject().create();

        return createIsLessThanProviderSwitcher(randomSupportSwitcher, max, parallelism);
    }

    DefaultMutationSupportContext create(final ParallelismSupportSettings parallelism, final RandomSupportSettings random) {
        ObjectSwitcher<RandomSupport> randomSupportSwitcher = random.createIsLessThanSwitcher(parallelism);
        ObjectSwitcher<GateProvider> shouldAddNodeMutationSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, addNodeMutationRate, parallelism);
        ObjectSwitcher<GateProvider> shouldAddConnectionMutationSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, addConnectionMutationRate, parallelism);
        ConnectionWeightProviderSwitchers connectionsWeightProviderSwitchers = createConnectionWeightProviderSwitchers(randomSupportSwitcher, parallelism);
        ObjectSwitcher<GateProvider> shouldDisableConnectionExpressedSwitcher = createIsLessThanProviderSwitcher(randomSupportSwitcher, disableConnectionExpressedRate, parallelism);

        return new DefaultMutationSupportContext(shouldAddNodeMutationSwitcher, shouldAddConnectionMutationSwitcher, connectionsWeightProviderSwitchers.perturb, connectionsWeightProviderSwitchers.replace, shouldDisableConnectionExpressedSwitcher);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ConnectionWeightProviderSwitchers {
        private final ObjectSwitcher<GateProvider> perturb;
        private final ObjectSwitcher<GateProvider> replace;
    }
}
