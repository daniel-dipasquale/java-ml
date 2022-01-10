package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class SpeciationSupport {
    @Builder.Default
    private final IntegerNumber maximumSpecies = IntegerNumber.literal(150);
    @Builder.Default
    private final FloatNumber weightDifferenceCoefficient = FloatNumber.literal(0.4f);
    @Builder.Default
    private final FloatNumber disjointCoefficient = FloatNumber.literal(1f);
    @Builder.Default
    private final FloatNumber excessCoefficient = FloatNumber.literal(1f);
    @Builder.Default
    private final FloatNumber compatibilityThreshold = FloatNumber.literal(3f);
    @Builder.Default
    private final FloatNumber compatibilityThresholdModifier = FloatNumber.literal(1f);
    @Builder.Default
    private final FloatNumber eugenicsThreshold = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber elitistThreshold = FloatNumber.literal(0.01f);
    @Builder.Default
    private final IntegerNumber elitistThresholdMinimum = IntegerNumber.literal(2);
    @Builder.Default
    private final IntegerNumber stagnationDropOffAge = IntegerNumber.literal(15);
    @Builder.Default
    private final FloatNumber interSpeciesMatingRate = FloatNumber.literal(0.001f);
    @Builder.Default
    private final FloatNumber mateOnlyRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber mutateOnlyRate = FloatNumber.literal(0.25f);

    ContextObjectSpeciationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport) {
        return ContextObjectSpeciationSupport.create(initializationContext, this, generalSupport);
    }
}
