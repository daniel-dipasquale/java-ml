package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultSpeciationSupportContext;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculatorDefault;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SpeciationSupportSettings {
    @Builder.Default
    private final IntegerNumberSettings maximumSpecies = null;
    @Builder.Default
    private final IntegerNumberSettings maximumGenomes = null;
    @Builder.Default
    private final FloatNumberSettings weightDifferenceCoefficient = FloatNumberSettings.literal(0.5f);
    @Builder.Default
    private final FloatNumberSettings disjointCoefficient = FloatNumberSettings.literal(1f);
    @Builder.Default
    private final FloatNumberSettings excessCoefficient = FloatNumberSettings.literal(1f);
    @Builder.Default
    private final FloatNumberSettings compatibilityThreshold = FloatNumberSettings.literal(3f);
    @Builder.Default
    private final FloatNumberSettings compatibilityThresholdModifier = FloatNumberSettings.literal(1.2f);
    @Builder.Default
    private final FloatNumberSettings eugenicsThreshold = FloatNumberSettings.literal(0.2f);
    @Builder.Default
    private final FloatNumberSettings elitistThreshold = FloatNumberSettings.literal(0.01f);
    @Builder.Default
    private final IntegerNumberSettings elitistThresholdMinimum = IntegerNumberSettings.literal(2);
    @Builder.Default
    private final IntegerNumberSettings stagnationDropOffAge = IntegerNumberSettings.literal(15);
    @Builder.Default
    private final FloatNumberSettings interSpeciesMatingRate = FloatNumberSettings.literal(0.001f);

    DefaultSpeciationSupportContext create(final GeneralEvaluatorSupportSettings general, final ParallelismSupportSettings parallelism) {
        int maximumSpeciesFixed = Optional.ofNullable(maximumSpecies)
                .map(sin -> sin.createFactorySwitcher(parallelism))
                .map(ObjectSwitcher::getObject)
                .map(IntegerFactory::create)
                .orElse(general.getPopulationSize() / 8);

        ArgumentValidatorSupport.ensureGreaterThanZero(maximumSpeciesFixed, "maximumSpecies");
        ArgumentValidatorSupport.ensureLessThan(maximumSpeciesFixed, general.getPopulationSize(), "maximumSpecies");

        int maximumGenomesFixed = Optional.ofNullable(maximumGenomes)
                .map(sin -> sin.createFactorySwitcher(parallelism))
                .map(ObjectSwitcher::getObject)
                .map(IntegerFactory::create)
                .orElse(general.getPopulationSize() / 2);

        ArgumentValidatorSupport.ensureGreaterThanZero(maximumGenomesFixed, "maximumGenomes");
        ArgumentValidatorSupport.ensureLessThan(maximumGenomesFixed, general.getPopulationSize(), "maximumGenomes");

        float weightDifferenceCoefficientFixed = weightDifferenceCoefficient.createFactorySwitcher(parallelism).getObject().create();
        float disjointCoefficientFixed = disjointCoefficient.createFactorySwitcher(parallelism).getObject().create();
        float excessCoefficientFixed = excessCoefficient.createFactorySwitcher(parallelism).getObject().create();
        float compatibilityThresholdFixed = compatibilityThreshold.createFactorySwitcher(parallelism).getObject().create();
        float compatibilityThresholdModifierFixed = compatibilityThresholdModifier.createFactorySwitcher(parallelism).getObject().create();
        GenomeCompatibilityCalculatorDefault genomeCompatibilityCalculator = new GenomeCompatibilityCalculatorDefault(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        float eugenicsThresholdFixed = eugenicsThreshold.createFactorySwitcher(parallelism).getObject().create();
        float elitistThresholdFixed = elitistThreshold.createFactorySwitcher(parallelism).getObject().create();
        int elitistThresholdMinimumFixed = elitistThresholdMinimum.createFactorySwitcher(parallelism).getObject().create();
        int stagnationDropOffAgeFixed = stagnationDropOffAge.createFactorySwitcher(parallelism).getObject().create();
        float interSpeciesMatingRateFixed = interSpeciesMatingRate.createFactorySwitcher(parallelism).getObject().create();

        return new DefaultSpeciationSupportContext(maximumSpeciesFixed, maximumGenomesFixed, compatibilityThresholdFixed, compatibilityThresholdModifierFixed, genomeCompatibilityCalculator, eugenicsThresholdFixed, elitistThresholdFixed, elitistThresholdMinimumFixed, stagnationDropOffAgeFixed, interSpeciesMatingRateFixed);
    }
}
