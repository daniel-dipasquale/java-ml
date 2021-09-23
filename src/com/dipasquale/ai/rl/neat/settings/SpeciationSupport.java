package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationParameters;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationSupport;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.MultiSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.ParallelUpdateSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.UpdateAllFitnessSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.UpdateSharedSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.GenesisSpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.MateAndMutateSpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.MultiSpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.PreserveMostFitSpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SharedFitnessAccumulatorSpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SpeciationSupport {
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
        float totalRate = mateOnlyRate * 2 + mutateOnlyRate * 2;
        OutputClassifier<ReproductionType> reproductionTypeClassifier = new OutputClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            reproductionTypeClassifier.addRangeFor(mateOnlyRate / totalRate, ReproductionType.MATE_ONLY);
            reproductionTypeClassifier.addRangeFor(mutateOnlyRate / totalRate, ReproductionType.MUTATE_ONLY);
        }

        reproductionTypeClassifier.addRemainingRangeFor(ReproductionType.MATE_AND_MUTATE);

        return reproductionTypeClassifier;
    }

    private static OutputClassifier<ReproductionType> createLessThan2ReproductionTypeClassifier(final float mutateOnlyRate) {
        OutputClassifier<ReproductionType> reproductionTypeClassifier = new OutputClassifier<>();

        if (Float.compare(mutateOnlyRate, 0f) > 0) {
            reproductionTypeClassifier.addRemainingRangeFor(ReproductionType.MUTATE_ONLY);
        } else {
            reproductionTypeClassifier.addRemainingRangeFor(ReproductionType.CLONE);
        }

        return reproductionTypeClassifier;
    }

    private DefaultReproductionTypeFactoryProfile createRandomReproductionTypeGeneratorProfile(final ParallelismSupport parallelismSupport, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair) {
        float _mateOnlyRate = mateOnlyRate.createFactoryProfile(parallelismSupport).getObject().create();
        float _mutateOnlyRate = mutateOnlyRate.createFactoryProfile(parallelismSupport).getObject().create();
        OutputClassifier<ReproductionType> reproductionTypeClassifier = createReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new DefaultReproductionTypeFactoryProfile(parallelismSupport.isEnabled(), randomSupportPair, reproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    private static ObjectProfile<SpeciesFitnessStrategy> createFitnessStrategyProfile(final ParallelismSupport parallelismSupport) {
        List<SpeciesFitnessStrategy> strategies = ImmutableList.<SpeciesFitnessStrategy>builder()
                .add(new ParallelUpdateSpeciesFitnessStrategy())
                .add(new UpdateSharedSpeciesFitnessStrategy())
                .build();

        SpeciesFitnessStrategy onStrategy = new MultiSpeciesFitnessStrategy(strategies);
        SpeciesFitnessStrategy offStrategy = new UpdateAllFitnessSpeciesFitnessStrategy();

        return new DefaultObjectProfile<>(parallelismSupport.isEnabled(), onStrategy, offStrategy);
    }

    private static ObjectProfile<SpeciesSelectionStrategyExecutor> createEvolutionStrategyProfile(final ParallelismSupport parallelismSupport) {
        List<SpeciesSelectionStrategy> strategies = ImmutableList.<SpeciesSelectionStrategy>builder()
                .add(new LeastFitRemoverSpeciesSelectionStrategy())
                .add(new SharedFitnessAccumulatorSpeciesSelectionStrategy())
                .add(new ChampionPromoterSpeciesSelectionStrategy())
                .build();

        return new DefaultObjectProfile<>(parallelismSupport.isEnabled(), new SpeciesSelectionStrategyExecutor(strategies));
    }

    private static ObjectProfile<SpeciesReproductionStrategy> createReproductionStrategyProfile(final ParallelismSupport parallelismSupport) {
        List<SpeciesReproductionStrategy> strategies = ImmutableList.<SpeciesReproductionStrategy>builder()
                .add(new PreserveMostFitSpeciesReproductionStrategy())
                .add(new MateAndMutateSpeciesReproductionStrategy())
                .add(new GenesisSpeciesReproductionStrategy())
                .build();

        SpeciesReproductionStrategy strategy = new MultiSpeciesReproductionStrategy(strategies);

        return new DefaultObjectProfile<>(parallelismSupport.isEnabled(), strategy);
    }

    DefaultContextSpeciationSupport create(final ParallelismSupport parallelismSupport, final RandomSupport randomSupport) {
        DefaultContextSpeciationParameters params = DefaultContextSpeciationParameters.builder()
                .compatibilityThreshold(compatibilityThreshold.createFactoryProfile(parallelismSupport).getObject().create())
                .compatibilityThresholdModifier(compatibilityThresholdModifier.createFactoryProfile(parallelismSupport).getObject().create())
                .eugenicsThreshold(eugenicsThreshold.createFactoryProfile(parallelismSupport).getObject().create())
                .elitistThreshold(elitistThreshold.createFactoryProfile(parallelismSupport).getObject().create())
                .elitistThresholdMinimum(elitistThresholdMinimum.createFactoryProfile(parallelismSupport).getObject().create())
                .stagnationDropOffAge(stagnationDropOffAge.createFactoryProfile(parallelismSupport).getObject().create())
                .interSpeciesMatingRate(interSpeciesMatingRate.createFactoryProfile(parallelismSupport).getObject().create())
                .build();

        float weightDifferenceCoefficientFixed = weightDifferenceCoefficient.createFactoryProfile(parallelismSupport).getObject().create();
        float disjointCoefficientFixed = disjointCoefficient.createFactoryProfile(parallelismSupport).getObject().create();
        float excessCoefficientFixed = excessCoefficient.createFactoryProfile(parallelismSupport).getObject().create();
        DualModeSequentialIdFactory speciesIdFactory = new DualModeSequentialIdFactory(parallelismSupport.isEnabled(), "species");
        DualModeGenomePool genomePool = new DualModeGenomePool(parallelismSupport.isEnabled());
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        ObjectProfile<com.dipasquale.common.random.float1.RandomSupport> randomSupportProfile = randomSupport.createFloatRandomSupportProfile(parallelismSupport);
        Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        DefaultReproductionTypeFactoryProfile randomReproductionTypeGeneratorProfile = createRandomReproductionTypeGeneratorProfile(parallelismSupport, randomSupportPair);
        ObjectProfile<SpeciesFitnessStrategy> fitnessStrategyProfile = createFitnessStrategyProfile(parallelismSupport);
        ObjectProfile<SpeciesSelectionStrategyExecutor> evolutionStrategyProfile = createEvolutionStrategyProfile(parallelismSupport);
        ObjectProfile<SpeciesReproductionStrategy> reproductionStrategyProfile = createReproductionStrategyProfile(parallelismSupport);

        return new DefaultContextSpeciationSupport(params, speciesIdFactory, genomePool, genomeCompatibilityCalculator, randomReproductionTypeGeneratorProfile, fitnessStrategyProfile, evolutionStrategyProfile, reproductionStrategyProfile);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultReproductionTypeFactory implements ObjectIndexer<ReproductionType>, Serializable {
        @Serial
        private static final long serialVersionUID = 6788712949153539093L;
        private final com.dipasquale.common.random.float1.RandomSupport randomSupport;
        private final OutputClassifier<ReproductionType> reproductionTypeClassifier;
        private final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int organisms) {
            float random = randomSupport.next();

            if (organisms >= 2) {
                return reproductionTypeClassifier.resolve(random);
            }

            return lessThan2ReproductionTypeClassifier.resolve(random);
        }
    }

    private static final class DefaultReproductionTypeFactoryProfile extends AbstractObjectProfile<ObjectIndexer<ReproductionType>> {
        @Serial
        private static final long serialVersionUID = -976465186511988776L;

        private DefaultReproductionTypeFactoryProfile(final boolean concurrent, final Pair<com.dipasquale.common.random.float1.RandomSupport> randomSupportPair, final OutputClassifier<ReproductionType> reproductionTypeClassifier, final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier) {
            super(concurrent, new DefaultReproductionTypeFactory(randomSupportPair.getLeft(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier), new DefaultReproductionTypeFactory(randomSupportPair.getRight(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier));
        }
    }
}
