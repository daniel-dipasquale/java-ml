package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class CrossOverSettings {
    @Builder.Default
    private final FloatNumber overrideExpressedConnectionRate = FloatNumber.constant(0.5f);
    @Builder.Default
    private final FloatNumber useWeightFromRandomParentRate = FloatNumber.constant(0.6f);

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "IsLessThanGateBuilder", builderMethodName = "isLessThanGateBuilder")
    private static GenerationalIsLessThanRandomGate createIsLessThanGate(final NeatInitializationContext initializationContext, final FloatNumber maximumRateFloatNumber, final String name) {
        RandomSupport randomSupport = initializationContext.createDefaultRandomSupport();
        FloatFactory maximumFloatFactory = maximumRateFloatNumber.createFactory(initializationContext, 0f, 1f, name);

        return new GenerationalIsLessThanRandomGate(randomSupport, maximumFloatFactory);
    }

    DefaultNeatContextCrossOverSupport create(final NeatInitializationContext initializationContext) {
        GenerationalIsLessThanRandomGate shouldOverrideExpressedConnectionGate = isLessThanGateBuilder()
                .initializationContext(initializationContext)
                .maximumRateFloatNumber(overrideExpressedConnectionRate)
                .build();

        GenerationalIsLessThanRandomGate shouldUseWeightFromRandomParentGate = isLessThanGateBuilder()
                .initializationContext(initializationContext)
                .maximumRateFloatNumber(useWeightFromRandomParentRate)
                .build();

        return new DefaultNeatContextCrossOverSupport(shouldOverrideExpressedConnectionGate, shouldUseWeightFromRandomParentGate);
    }
}
