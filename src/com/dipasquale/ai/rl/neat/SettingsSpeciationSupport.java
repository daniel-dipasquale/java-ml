package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultSpeciationSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculatorDefault;
import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.concurrent.IntegerBiFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsSpeciationSupport {
    @Builder.Default
    private final SettingsIntegerNumber maximumSpecies = null;
    @Builder.Default
    private final SettingsIntegerNumber maximumGenomes = null;
    @Builder.Default
    private final SettingsFloatNumber weightDifferenceCoefficient = SettingsFloatNumber.literal(0.5f);
    @Builder.Default
    private final SettingsFloatNumber disjointCoefficient = SettingsFloatNumber.literal(1f);
    @Builder.Default
    private final SettingsFloatNumber excessCoefficient = SettingsFloatNumber.literal(1f);
    @Builder.Default
    private final SettingsFloatNumber compatibilityThreshold = SettingsFloatNumber.literal(3f);
    @Builder.Default
    private final SettingsFloatNumber compatibilityThresholdModifier = SettingsFloatNumber.literal(1.2f);
    @Builder.Default
    private final SettingsFloatNumber eugenicsThreshold = SettingsFloatNumber.literal(0.2f);
    @Builder.Default
    private final SettingsFloatNumber elitistThreshold = SettingsFloatNumber.literal(0.01f);
    @Builder.Default
    private final SettingsIntegerNumber elitistThresholdMinimum = SettingsIntegerNumber.literal(2);
    @Builder.Default
    private final SettingsIntegerNumber stagnationDropOffAge = SettingsIntegerNumber.literal(15);
    @Builder.Default
    private final SettingsFloatNumber interSpeciesMatingRate = SettingsFloatNumber.literal(0.001f);

    ContextDefaultSpeciationSupport create(final SettingsGeneralEvaluatorSupport general, final SettingsParallelismSupport parallelism) {
        int maximumSpeciesFixed = Optional.ofNullable(maximumSpecies)
                .map(sin -> sin.createFactory(parallelism))
                .map(IntegerBiFactory::create)
                .orElse(general.getPopulationSize() / 8);

        ArgumentValidatorUtils.ensureGreaterThanZero(maximumSpeciesFixed, "maximumSpecies");
        ArgumentValidatorUtils.ensureLessThan(maximumSpeciesFixed, general.getPopulationSize(), "maximumSpecies");

        int maximumGenomesFixed = Optional.ofNullable(maximumGenomes)
                .map(sin -> sin.createFactory(parallelism))
                .map(IntegerBiFactory::create)
                .orElse(general.getPopulationSize() / 2);

        ArgumentValidatorUtils.ensureGreaterThanZero(maximumGenomesFixed, "maximumGenomes");
        ArgumentValidatorUtils.ensureLessThan(maximumGenomesFixed, general.getPopulationSize(), "maximumGenomes");

        float weightDifferenceCoefficientFixed = weightDifferenceCoefficient.createFactory(parallelism).create();
        float disjointCoefficientFixed = disjointCoefficient.createFactory(parallelism).create();
        float excessCoefficientFixed = excessCoefficient.createFactory(parallelism).create();
        float compatibilityThresholdFixed = compatibilityThreshold.createFactory(parallelism).create();
        float compatibilityThresholdModifierFixed = compatibilityThresholdModifier.createFactory(parallelism).create();
        GenomeCompatibilityCalculatorDefault genomeCompatibilityCalculator = new GenomeCompatibilityCalculatorDefault(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        float eugenicsThresholdFixed = eugenicsThreshold.createFactory(parallelism).create();
        float elitistThresholdFixed = elitistThreshold.createFactory(parallelism).create();
        int elitistThresholdMinimumFixed = elitistThresholdMinimum.createFactory(parallelism).create();
        int stagnationDropOffAgeFixed = stagnationDropOffAge.createFactory(parallelism).create();
        float interSpeciesMatingRateFixed = interSpeciesMatingRate.createFactory(parallelism).create();

        return new ContextDefaultSpeciationSupport(maximumSpeciesFixed, maximumGenomesFixed, compatibilityThresholdFixed, compatibilityThresholdModifierFixed, genomeCompatibilityCalculator, eugenicsThresholdFixed, elitistThresholdFixed, elitistThresholdMinimumFixed, stagnationDropOffAgeFixed, interSpeciesMatingRateFixed);
    }
}
