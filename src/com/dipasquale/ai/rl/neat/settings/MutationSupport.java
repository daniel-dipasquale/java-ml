/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultMutationSupportContext;
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
public final class MutationSupport {
    @Builder.Default
    private final FloatNumber addNodeMutationRate = FloatNumber.literal(0.1f);
    @Builder.Default
    private final FloatNumber addConnectionMutationRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber perturbConnectionsWeightRate = FloatNumber.literal(0.75f);
    @Builder.Default
    private final FloatNumber replaceConnectionsWeightRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber disableConnectionExpressedRate = FloatNumber.literal(0.05f);

    private static ObjectSwitcher<GateProvider> createLiteralProviderSwitcher(final boolean isOn, final ParallelismSupport parallelism) {
        return new DefaultObjectSwitcher<>(parallelism.isEnabled(), new LiteralGateProvider(isOn));
    }

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final float max, final ParallelismSupport parallelism) {
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectSwitcher.deconstruct(randomSupportSwitcher);

        return new IsLessThanRandomGateProviderSwitcher(parallelism.isEnabled(), randomSupportPair, max);
    }

    private ConnectionWeightProviderSwitchers createConnectionWeightProviderSwitchers(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final ParallelismSupport parallelism) {
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

    private static ObjectSwitcher<GateProvider> createIsLessThanProviderSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final FloatNumber maximumNumber, final ParallelismSupport parallelism) {
        float max = maximumNumber.createFactorySwitcher(parallelism).getObject().create();

        return createIsLessThanProviderSwitcher(randomSupportSwitcher, max, parallelism);
    }

    DefaultMutationSupportContext create(final ParallelismSupport parallelism, final RandomSupport random) {
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher = random.createIsLessThanSwitcher(parallelism);
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
