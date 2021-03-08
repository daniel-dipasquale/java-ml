package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsSpeciation {
    private final SettingsIntegerNumber maximumGenomes;
    @Builder.Default
    private final SettingsFloatNumber weightDifferenceCoefficient = SettingsFloatNumber.literal(1f);
    @Builder.Default
    private final SettingsFloatNumber disjointCoefficient = SettingsFloatNumber.literal(2f);
    @Builder.Default
    private final SettingsFloatNumber excessCoefficient = SettingsFloatNumber.literal(2f);
    @Builder.Default
    private final SettingsFloatNumber compatibilityThreshold = SettingsFloatNumber.literal(6f);
    @Builder.Default
    private final SettingsFloatNumber compatibilityThresholdModifier = SettingsFloatNumber.literal(1f);
    @Builder.Default
    private final SettingsFloatNumber eugenicsThreshold = SettingsFloatNumber.literal(0.2f);
    @Builder.Default
    private final SettingsFloatNumber elitistThreshold = SettingsFloatNumber.literal(0.01f);
    @Builder.Default
    private final SettingsIntegerNumber elitistThresholdMinimum = SettingsIntegerNumber.literal(1);
    @Builder.Default
    private final SettingsIntegerNumber stagnationDropOffAge = SettingsIntegerNumber.literal(15);
    @Builder.Default
    private final SettingsFloatNumber interspeciesMatingRate = SettingsFloatNumber.literal(0.001f);

    ContextDefaultComponentFactory<ContextDefaultSpeciation> createFactory(final SettingsGeneralSupport general) {
        return c -> {
            SettingsIntegerNumber maximumGenomesFixed = Optional.ofNullable(maximumGenomes)
                    .orElseGet(() -> SettingsIntegerNumber.literal(general.getPopulationSize() / 2));

            GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(c);

            return new ContextDefaultSpeciation(maximumGenomesFixed.get(), weightDifferenceCoefficient.get(), disjointCoefficient.get(), excessCoefficient.get(), compatibilityThreshold.get(), compatibilityThresholdModifier.get(), genomeCompatibilityCalculator, eugenicsThreshold.get(), elitistThreshold.get(), elitistThresholdMinimum.get(), stagnationDropOffAge.get(), interspeciesMatingRate.get());
        };
    }
}
