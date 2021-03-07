package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsSpeciation {
    private final SettingsIntegerNumber maximumGenomes;
    private final SettingsFloatNumber weightDifferenceCoefficient;
    private final SettingsFloatNumber disjointCoefficient;
    private final SettingsFloatNumber excessCoefficient;
    private final SettingsFloatNumber compatibilityThreshold;
    private final SettingsFloatNumber compatibilityThresholdModifier;
    private final SettingsFloatNumber survivalThreshold;
    private final SettingsFloatNumber elitistThreshold;
    private final SettingsIntegerNumber elitistThresholdMinimum;
    private final SettingsIntegerNumber stagnationDropOffAge;
    private final SettingsFloatNumber interspeciesMatingRate;

    ContextDefaultComponentFactory<ContextDefaultSpeciation> createFactory() {
        return c -> {
            GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(c.speciation());

            return new ContextDefaultSpeciation(maximumGenomes.get(), weightDifferenceCoefficient.get(), disjointCoefficient.get(), excessCoefficient.get(), compatibilityThreshold.get(), compatibilityThresholdModifier.get(), genomeCompatibilityCalculator, survivalThreshold.get(), elitistThreshold.get(), elitistThresholdMinimum.get(), stagnationDropOffAge.get(), interspeciesMatingRate.get());
        };
    }
}
