package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.OutputClassifier;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationParameters;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationSupport;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.factory.ObjectAccessor;
import com.dipasquale.common.switcher.AbstractObjectSwitcher;
import com.dipasquale.common.switcher.ObjectSwitcher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
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
    @Builder.Default
    private final FloatNumber mateOnlyRate = FloatNumber.literal(0.2f);
    @Builder.Default
    private final FloatNumber mutateOnlyRate = FloatNumber.literal(0.25f);

    private static OutputClassifier<ReproductionType> createReproductionTypeClassifier(final float mateOnlyRate, final float mutateOnlyRate) {
        float totalRate = mateOnlyRate * mutateOnlyRate + mateOnlyRate + mutateOnlyRate;
        float totalRateFixed = (float) Math.ceil(totalRate);
        OutputClassifier<ReproductionType> reproductionTypeClassifier = new OutputClassifier<>();

        reproductionTypeClassifier.AddUpUntil(ReproductionType.MATE_AND_MUTATE, mateOnlyRate * mutateOnlyRate / totalRateFixed);
        reproductionTypeClassifier.AddUpUntil(ReproductionType.MATE_ONLY, mateOnlyRate / totalRateFixed);
        reproductionTypeClassifier.AddUpUntil(ReproductionType.MUTATE_ONLY, mutateOnlyRate / totalRateFixed);
        reproductionTypeClassifier.addOtherwiseRoundedUp(ReproductionType.CLONE);

        return reproductionTypeClassifier;
    }

    private static OutputClassifier<ReproductionType> createLessThan2ReproductionTypeClassifier(final float mutateOnlyRate) {
        float totalRate = (float) Math.ceil(mutateOnlyRate);
        OutputClassifier<ReproductionType> reproductionTypeClassifier = new OutputClassifier<>();

        reproductionTypeClassifier.AddUpUntil(ReproductionType.MUTATE_ONLY, mutateOnlyRate / totalRate);
        reproductionTypeClassifier.addOtherwiseRoundedUp(ReproductionType.CLONE);

        return reproductionTypeClassifier;
    }

    private DefaultReproductionTypeFactorySwitcher createRandomReproductionTypeGeneratorSwitcher(final ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher, final ParallelismSupport parallelism) {
        float _mateOnlyRate = mateOnlyRate.createFactorySwitcher(parallelism).getObject().create();
        float _mutateOnlyRate = mutateOnlyRate.createFactorySwitcher(parallelism).getObject().create();
        OutputClassifier<ReproductionType> reproductionTypeClassifier = createReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new DefaultReproductionTypeFactorySwitcher(parallelism.isEnabled(), ObjectSwitcher.deconstruct(randomSupportSwitcher), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    DefaultContextSpeciationSupport create(final GeneralEvaluatorSupport general, final ParallelismSupport parallelism, final RandomSupport random) {
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

        DefaultContextSpeciationParameters params = DefaultContextSpeciationParameters.builder()
                .maximumSpecies(maximumSpeciesFixed)
                .maximumGenomes(maximumGenomesFixed)
                .compatibilityThreshold(compatibilityThreshold.createFactorySwitcher(parallelism).getObject().create())
                .compatibilityThresholdModifier(compatibilityThresholdModifier.createFactorySwitcher(parallelism).getObject().create())
                .eugenicsThreshold(eugenicsThreshold.createFactorySwitcher(parallelism).getObject().create())
                .elitistThreshold(elitistThreshold.createFactorySwitcher(parallelism).getObject().create())
                .elitistThresholdMinimum(elitistThresholdMinimum.createFactorySwitcher(parallelism).getObject().create())
                .stagnationDropOffAge(stagnationDropOffAge.createFactorySwitcher(parallelism).getObject().create())
                .interSpeciesMatingRate(interSpeciesMatingRate.createFactorySwitcher(parallelism).getObject().create())
                .build();

        float weightDifferenceCoefficientFixed = weightDifferenceCoefficient.createFactorySwitcher(parallelism).getObject().create();
        float disjointCoefficientFixed = disjointCoefficient.createFactorySwitcher(parallelism).getObject().create();
        float excessCoefficientFixed = excessCoefficient.createFactorySwitcher(parallelism).getObject().create();
        DefaultGenomeCompatibilityCalculator genomeCompatibilityCalculator = new DefaultGenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        ObjectSwitcher<com.dipasquale.common.random.float1.RandomSupport> randomSupportSwitcher = random.createIsLessThanSwitcher(parallelism);
        DefaultReproductionTypeFactorySwitcher randomReproductionTypeGeneratorSwitcher = createRandomReproductionTypeGeneratorSwitcher(randomSupportSwitcher, parallelism);

        return new DefaultContextSpeciationSupport(params, genomeCompatibilityCalculator, randomReproductionTypeGeneratorSwitcher);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultReproductionTypeFactory implements ObjectAccessor<ReproductionType>, Serializable {
        @Serial
        private static final long serialVersionUID = -3577796023989570060L;
        private final com.dipasquale.common.random.float1.RandomSupport randomSupport;
        private final OutputClassifier<ReproductionType> reproductionTypeClassifier;
        private final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int value) {
            float random = randomSupport.next();

            if (value >= 2) {
                return reproductionTypeClassifier.resolve(random);
            }

            return lessThan2ReproductionTypeClassifier.resolve(random);
        }
    }

    private static final class DefaultReproductionTypeFactorySwitcher extends AbstractObjectSwitcher<ObjectAccessor<ReproductionType>> {
        @Serial
        private static final long serialVersionUID = -5289301053039117522L;

        private DefaultReproductionTypeFactorySwitcher(final boolean isOn,
                                                       final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair,
                                                       final OutputClassifier<ReproductionType> reproductionTypeClassifier,
                                                       final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier) { // TODO: fix this, Pair<com.dipasquale.common.random.float1.RandomSupport> goes against the idea of controlling the singleton of it @com.dipasquale.ai.rl.neat.switcher.factory.Constants
            super(isOn, new DefaultReproductionTypeFactory(randomSupportPair.getLeft(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier), new DefaultReproductionTypeFactory(randomSupportPair.getRight(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier));
        }
    }
}
