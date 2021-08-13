/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultSpeciationSupportContext;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenomeCompatibilityCalculator;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SpeciationSupport {
    @Builder.Default
    private final IntegerNumber maximumSpecies = null;
    @Builder.Default
    private final IntegerNumber maximumGenomes = null;
    @Builder.Default
    private final FloatNumber weightDifferenceCoefficient = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber disjointCoefficient = FloatNumber.literal(1f);
    @Builder.Default
    private final FloatNumber excessCoefficient = FloatNumber.literal(1f);
    @Builder.Default
    private final FloatNumber compatibilityThreshold = FloatNumber.literal(3f);
    @Builder.Default
    private final FloatNumber compatibilityThresholdModifier = FloatNumber.literal(1.2f);
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

    DefaultSpeciationSupportContext create(final GeneralEvaluatorSupport general, final ParallelismSupport parallelism) {
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
        DefaultGenomeCompatibilityCalculator genomeCompatibilityCalculator = new DefaultGenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        float eugenicsThresholdFixed = eugenicsThreshold.createFactorySwitcher(parallelism).getObject().create();
        float elitistThresholdFixed = elitistThreshold.createFactorySwitcher(parallelism).getObject().create();
        int elitistThresholdMinimumFixed = elitistThresholdMinimum.createFactorySwitcher(parallelism).getObject().create();
        int stagnationDropOffAgeFixed = stagnationDropOffAge.createFactorySwitcher(parallelism).getObject().create();
        float interSpeciesMatingRateFixed = interSpeciesMatingRate.createFactorySwitcher(parallelism).getObject().create();

        return new DefaultSpeciationSupportContext(maximumSpeciesFixed, maximumGenomesFixed, compatibilityThresholdFixed, compatibilityThresholdModifierFixed, genomeCompatibilityCalculator, eugenicsThresholdFixed, elitistThresholdFixed, elitistThresholdMinimumFixed, stagnationDropOffAgeFixed, interSpeciesMatingRateFixed);
    }
}
