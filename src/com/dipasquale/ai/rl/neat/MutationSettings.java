package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.generational.factory.GenerationalWeightMutationTypeFactory;
import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class MutationSettings {
    @Builder.Default
    private final FloatNumber addNodeRate = FloatNumber.constant(0.03f);
    @Builder.Default
    private final FloatNumber addConnectionRate = FloatNumber.constant(0.06f);
    @Builder.Default
    private final FloatNumber perturbWeightRate = FloatNumber.constant(0.75f);
    @Builder.Default
    private final FloatNumber replaceWeightRate = FloatNumber.constant(0.5f);
    @Builder.Default
    private final FloatNumber disableExpressedConnectionRate = FloatNumber.constant(0.015f);

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "IsLessThanGateBuilder", builderMethodName = "isLessThanGateBuilder")
    private static GenerationalIsLessThanRandomGate createIsLessThanGate(final NeatInitializationContext initializationContext, final FloatNumber maximumRateFloatNumber, final String name) {
        RandomSupport randomSupport = initializationContext.createDefaultRandomSupport();
        FloatFactory maximumFloatFactory = maximumRateFloatNumber.createFactory(initializationContext, 0f, 1f, name);

        return new GenerationalIsLessThanRandomGate(randomSupport, maximumFloatFactory);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "FloatFactoryBuilder", builderMethodName = "floatFactoryBuilder")
    private static FloatFactory createFloatFactory(final NeatInitializationContext initializationContext, final FloatNumber floatNumber, final String name) {
        return floatNumber.createFactory(initializationContext, 0f, 1f, name);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "WeightMutationTypeFactoryBuilder", builderMethodName = "weightMutationTypeFactoryBuilder")
    private static GenerationalWeightMutationTypeFactory createWeightMutationTypeFactory(final RandomSupport randomSupport, final FloatFactory perturbRateFloatFactory, final FloatFactory replaceRateFloatFactory) {
        return new GenerationalWeightMutationTypeFactory(randomSupport, perturbRateFloatFactory, replaceRateFloatFactory);
    }

    DefaultNeatContextMutationSupport create(final NeatInitializationContext initializationContext) {
        GenerationalIsLessThanRandomGate shouldAddNodeGate = isLessThanGateBuilder()
                .initializationContext(initializationContext)
                .maximumRateFloatNumber(addNodeRate)
                .name("mutation.addNodeRate")
                .build();

        GenerationalIsLessThanRandomGate shouldAddConnectionGate = isLessThanGateBuilder()
                .initializationContext(initializationContext)
                .maximumRateFloatNumber(addConnectionRate)
                .name("mutation.addConnectionRate")
                .build();

        GenerationalWeightMutationTypeFactory weightMutationTypeFactory = weightMutationTypeFactoryBuilder()
                .randomSupport(initializationContext.createDefaultRandomSupport())
                .perturbRateFloatFactory(floatFactoryBuilder()
                        .initializationContext(initializationContext)
                        .floatNumber(perturbWeightRate)
                        .name("mutation.perturbWeightRate")
                        .build())
                .replaceRateFloatFactory(floatFactoryBuilder()
                        .initializationContext(initializationContext)
                        .floatNumber(replaceWeightRate)
                        .name("mutation.replaceWeightRate")
                        .build())
                .build();

        GenerationalIsLessThanRandomGate shouldDisableExpressedConnectionGate = isLessThanGateBuilder()
                .initializationContext(initializationContext)
                .maximumRateFloatNumber(disableExpressedConnectionRate)
                .name("mutation.disableExpressedConnectionRate")
                .build();

        return new DefaultNeatContextMutationSupport(shouldAddNodeGate, shouldAddConnectionGate, weightMutationTypeFactory, shouldDisableExpressedConnectionGate);
    }
}
