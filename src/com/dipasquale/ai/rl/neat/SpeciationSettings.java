package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.PopulationSizeProvider;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalEveryReproductionTypeFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalFloatFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalGenomeCompatibilityCalculatorFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalIntegerFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalMinIntegerFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalReproductionTypeProvider;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalSingleOrganismReproductionTypeFactory;
import com.dipasquale.ai.rl.neat.genotype.GenomePool;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.factory.IntegerFactory;
import com.dipasquale.common.random.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SpeciationSettings {
    @Builder.Default
    private final IntegerNumber populationSize = IntegerNumber.constant(150);
    @Builder.Default
    private final IntegerNumber maximumSpecies = IntegerNumber.constant(150);
    @Builder.Default
    private final FloatNumber weightDifferenceCoefficient = FloatNumber.constant(0.4f);
    @Builder.Default
    private final FloatNumber disjointCoefficient = FloatNumber.constant(1f);
    @Builder.Default
    private final FloatNumber excessCoefficient = FloatNumber.constant(1f);
    @Builder.Default
    private final FloatNumber compatibilityThreshold = FloatNumber.constant(3f);
    @Builder.Default
    private final FloatNumber compatibilityThresholdModifier = FloatNumber.constant(1f);
    @Builder.Default
    private final FloatNumber eugenicsThreshold = FloatNumber.constant(0.2f);
    @Builder.Default
    private final FloatNumber elitistThreshold = FloatNumber.constant(0.01f);
    @Builder.Default
    private final IntegerNumber minimumElitistDesired = IntegerNumber.constant(2);
    @Builder.Default
    private final IntegerNumber stagnationDropOffAge = IntegerNumber.constant(15);
    @Builder.Default
    private final FloatNumber interSpeciesMatingRate = FloatNumber.constant(0.001f);
    @Builder.Default
    private final FloatNumber mateOnlyRate = FloatNumber.constant(0.2f);
    @Builder.Default
    private final FloatNumber mutateOnlyRate = FloatNumber.constant(0.25f);

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "GenerationalFloatFactoryBuilder", builderMethodName = "generationalFloatFactoryBuilder")
    private static GenerationalFloatFactory createGenerationalFloatFactory(final NeatInitializationContext initializationContext, final FloatNumber floatNumber, final float minimum, final float maximum, final String name) {
        FloatFactory floatFactory = floatNumber.createFactory(initializationContext, minimum, maximum, name);

        return new GenerationalFloatFactory(floatFactory);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "IntegerFactoryBuilder", builderMethodName = "integerFactoryBuilder")
    private static IntegerFactory createIntegerFactory(final NeatInitializationContext initializationContext, final IntegerNumber integerNumber, final int minimum, final int maximum, final String name) {
        return integerNumber.createFactory(initializationContext, minimum, maximum, name);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "PopulationSizeProviderBuilder", builderMethodName = "populationSizeProviderBuilder")
    private static PopulationSizeProvider createPopulationSizeProvider(final NeatInitializationContext initializationContext, final IntegerNumber populationSize) {
        IntegerFactory integerFactory = createIntegerFactory(initializationContext, populationSize, 20, Integer.MAX_VALUE, "speciation.populationSize");

        return new PopulationSizeProvider(integerFactory);
    }

    @Builder(access = AccessLevel.PRIVATE, builderClassName = "GenerationalIntegerFactoryBuilder", builderMethodName = "generationalIntegerFactoryBuilder")
    private static GenerationalIntegerFactory createGenerationalIntegerFactory(final NeatInitializationContext initializationContext, final IntegerNumber integerNumber, final int minimum, final int maximum, final String name) {
        IntegerFactory integerFactory = createIntegerFactory(initializationContext, integerNumber, minimum, maximum, name);

        return new GenerationalIntegerFactory(integerFactory);
    }

    GenerationalMinIntegerFactory createMaximumSpeciesGenerationalIntegerFactory(final NeatInitializationContext initializationContext, final IntegerNumber maximumSpeciesFloatNumber, final PopulationSizeProvider populationSizeProvider) {
        IntegerFactory maximumSpeciesIntegerFactory = maximumSpeciesFloatNumber.createFactory(initializationContext, 0, "speciation.maximumSpecies");

        return new GenerationalMinIntegerFactory(maximumSpeciesIntegerFactory, populationSizeProvider.toFactory());
    }

    private static GenerationalGenomeCompatibilityCalculatorFactory createGenomeCompatibilityCalculatorFactory(final NeatInitializationContext initializationContext, final SpeciationSettings speciationSettings) {
        GenerationalFloatFactory excessCoefficient = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(speciationSettings.excessCoefficient)
                .minimum(0f)
                .maximum(Float.MAX_VALUE)
                .name("speciation.excessCoefficient")
                .build();

        GenerationalFloatFactory disjointCoefficient = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(speciationSettings.disjointCoefficient)
                .minimum(0f)
                .maximum(Float.MAX_VALUE)
                .name("speciation.disjointCoefficient")
                .build();

        GenerationalFloatFactory weightDifferenceCoefficient = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(speciationSettings.weightDifferenceCoefficient)
                .minimum(0f)
                .maximum(Float.MAX_VALUE)
                .name("speciation.weightDifferenceCoefficient")
                .build();

        return new GenerationalGenomeCompatibilityCalculatorFactory(excessCoefficient, disjointCoefficient, weightDifferenceCoefficient);
    }

    private static GenerationalEveryReproductionTypeFactory createEveryReproductionTypeFactory(final NeatInitializationContext initializationContext, final SpeciationSettings speciationSettings) {
        RandomSupport randomSupport = initializationContext.createDefaultRandomSupport();
        FloatFactory mateOnlyRateFloatFactory = speciationSettings.mateOnlyRate.createFactory(initializationContext, 0f, 1f, "speciation.mateOnlyRate");
        FloatFactory mutateOnlyRateFloatFactory = speciationSettings.mutateOnlyRate.createFactory(initializationContext, 0f, 1f, "speciation.mutateOnlyRate");

        return new GenerationalEveryReproductionTypeFactory(randomSupport, mateOnlyRateFloatFactory, mutateOnlyRateFloatFactory);
    }

    private static GenerationalSingleOrganismReproductionTypeFactory createSingleOrganismReproductionTypeFactory(final NeatInitializationContext initializationContext, final FloatNumber mutateRateFloatNumber) {
        RandomSupport randomSupport = initializationContext.createDefaultRandomSupport();
        FloatFactory mutateRateFloatFactory = mutateRateFloatNumber.createFactory(initializationContext, 0f, 1f, "speciation.mutateOnlyRate");

        return new GenerationalSingleOrganismReproductionTypeFactory(randomSupport, mutateRateFloatFactory);
    }

    private static GenerationalReproductionTypeProvider createReproductionTypeProvider(final NeatInitializationContext initializationContext, final SpeciationSettings speciationSettings) {
        GenerationalEveryReproductionTypeFactory everyReproductionTypeFactory = createEveryReproductionTypeFactory(initializationContext, speciationSettings);
        GenerationalSingleOrganismReproductionTypeFactory singleOrganismReproductionTypeFactory = createSingleOrganismReproductionTypeFactory(initializationContext, speciationSettings.mutateOnlyRate);

        return new GenerationalReproductionTypeProvider(everyReproductionTypeFactory, singleOrganismReproductionTypeFactory);
    }

    DefaultNeatContextSpeciationSupport create(final NeatInitializationContext initializationContext) {
        NeatEnvironmentType environmentType = initializationContext.getEnvironmentType();

        PopulationSizeProvider populationSizeProvider = populationSizeProviderBuilder()
                .initializationContext(initializationContext)
                .populationSize(populationSize)
                .build();

        GenerationalMinIntegerFactory maximumSpeciesIntegerFactory = createMaximumSpeciesGenerationalIntegerFactory(initializationContext, maximumSpecies, populationSizeProvider);

        IdFactory speciesIdFactory = new IdFactory(IdType.SPECIES);
        GenomePool genomePool = new GenomePool();


        GenerationalFloatFactory compatibilityThresholdFloatFactory = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(compatibilityThreshold)
                .minimum(0f)
                .maximum(Float.MAX_VALUE)
                .name("speciation.compatibilityThreshold")
                .build();

        GenerationalFloatFactory compatibilityThresholdModifierFloatFactory = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(compatibilityThresholdModifier)
                .minimum(0f)
                .maximum(Float.MAX_VALUE)
                .name("speciation.compatibilityThresholdModifier")
                .build();

        GenerationalGenomeCompatibilityCalculatorFactory genomeCompatibilityCalculator = createGenomeCompatibilityCalculatorFactory(initializationContext, this);

        GenerationalFloatFactory eugenicsThresholdFloatFactory = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(eugenicsThreshold)
                .minimum(0f)
                .maximum(1f)
                .name("speciation.eugenicsThreshold")
                .build();

        GenerationalFloatFactory elitistThresholdFloatFactory = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(elitistThreshold)
                .minimum(0f)
                .maximum(1f)
                .name("speciation.elitistThreshold")
                .build();

        GenerationalIntegerFactory minimumElitistDesiredIntegerFactory = generationalIntegerFactoryBuilder()
                .initializationContext(initializationContext)
                .integerNumber(minimumElitistDesired)
                .minimum(0)
                .maximum(Integer.MAX_VALUE)
                .name("speciation.minimumElitistDesired")
                .build();

        GenerationalIntegerFactory stagnationDropOffAgeIntegerFactory = generationalIntegerFactoryBuilder()
                .initializationContext(initializationContext)
                .integerNumber(stagnationDropOffAge)
                .minimum(0)
                .maximum(Integer.MAX_VALUE)
                .name("speciation.stagnationDropOffAge")
                .build();

        GenerationalFloatFactory interSpeciesMatingRateFloatFactory = generationalFloatFactoryBuilder()
                .initializationContext(initializationContext)
                .floatNumber(interSpeciesMatingRate)
                .minimum(0f)
                .maximum(1f)
                .name("speciation.interSpeciesMatingRate")
                .build();

        GenerationalReproductionTypeProvider reproductionTypeProvider = createReproductionTypeProvider(initializationContext, this);
        int concurrencyLevel = initializationContext.getThreadIds().size();

        return new DefaultNeatContextSpeciationSupport(environmentType, populationSizeProvider, maximumSpeciesIntegerFactory, speciesIdFactory, genomePool, compatibilityThresholdFloatFactory, compatibilityThresholdModifierFloatFactory, genomeCompatibilityCalculator, eugenicsThresholdFloatFactory, elitistThresholdFloatFactory, minimumElitistDesiredIntegerFactory, reproductionTypeProvider, stagnationDropOffAgeIntegerFactory, interSpeciesMatingRateFloatFactory, concurrencyLevel);
    }
}
