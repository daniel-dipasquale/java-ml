package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultSpeciation;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculatorDefault;
import com.dipasquale.common.ArgumentValidatorUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsSpeciation {
    private final SettingsIntegerNumber maximumSpecies;
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
    private final SettingsFloatNumber interSpeciesMatingRate = SettingsFloatNumber.literal(0.01f);

    ContextDefaultComponentFactory<ContextDefaultSpeciation> createFactory(final SettingsGeneralEvaluatorSupport general) {
        return c -> {
            int maximumSpeciesFixed = Optional.ofNullable(maximumSpecies)
                    .map(SettingsIntegerNumber::get)
                    .orElseGet(() -> general.getPopulationSize() / 8);

            ArgumentValidatorUtils.ensureGreaterThanZero(maximumSpeciesFixed, "maximumSpecies");
            ArgumentValidatorUtils.ensureLessThan(maximumSpeciesFixed, general.getPopulationSize(), "maximumSpecies");

            int maximumGenomesFixed = Optional.ofNullable(maximumGenomes)
                    .map(SettingsIntegerNumber::get)
                    .orElseGet(() -> general.getPopulationSize() / 2);

            ArgumentValidatorUtils.ensureGreaterThanZero(maximumGenomesFixed, "maximumGenomes");
            ArgumentValidatorUtils.ensureLessThan(maximumGenomesFixed, general.getPopulationSize(), "maximumGenomes");

            GenomeCompatibilityCalculatorDefault genomeCompatibilityCalculator = new GenomeCompatibilityCalculatorDefault(c);

            return new ContextDefaultSpeciation(maximumSpeciesFixed, maximumGenomesFixed, weightDifferenceCoefficient.get(), disjointCoefficient.get(), excessCoefficient.get(), compatibilityThreshold.get(), compatibilityThresholdModifier.get(), genomeCompatibilityCalculator, eugenicsThreshold.get(), elitistThreshold.get(), elitistThresholdMinimum.get(), stagnationDropOffAge.get(), interSpeciesMatingRate.get());
        };
    }
}
