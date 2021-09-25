package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
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
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.common.Pair;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.AbstractObjectProfile;
import com.dipasquale.synchronization.dual.profile.DefaultObjectProfile;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private DualModeSequentialIdFactory speciesIdFactory;
    private DualModeGenomePool genomePool;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private ObjectProfile<ObjectIndexer<ReproductionType>> reproductionTypeFactoryProfile;
    private ObjectProfile<SpeciesFitnessStrategy> fitnessStrategyProfile;
    private ObjectProfile<SpeciesSelectionStrategyExecutor> selectionStrategyProfile;
    private ObjectProfile<SpeciesReproductionStrategy> reproductionStrategyProfile;

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

    private static DefaultReproductionTypeFactoryProfile createRandomReproductionTypeGeneratorProfile(final ParallelismSupport parallelismSupport, final FloatNumber mateOnlyRate, final FloatNumber mutateOnlyRate, final Pair<RandomSupport> randomSupportPair) {
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

    public static DefaultContextSpeciationSupport create(final ParallelismSupport parallelismSupport, final ObjectProfile<RandomSupport> randomSupportProfile, final SpeciationSupport speciationSupport) {
        DefaultContextSpeciationParameters params = DefaultContextSpeciationParameters.builder()
                .compatibilityThreshold(speciationSupport.getCompatibilityThreshold().createFactoryProfile(parallelismSupport).getObject().create())
                .compatibilityThresholdModifier(speciationSupport.getCompatibilityThresholdModifier().createFactoryProfile(parallelismSupport).getObject().create())
                .eugenicsThreshold(speciationSupport.getEugenicsThreshold().createFactoryProfile(parallelismSupport).getObject().create())
                .elitistThreshold(speciationSupport.getElitistThreshold().createFactoryProfile(parallelismSupport).getObject().create())
                .elitistThresholdMinimum(speciationSupport.getElitistThresholdMinimum().createFactoryProfile(parallelismSupport).getObject().create())
                .stagnationDropOffAge(speciationSupport.getStagnationDropOffAge().createFactoryProfile(parallelismSupport).getObject().create())
                .interSpeciesMatingRate(speciationSupport.getInterSpeciesMatingRate().createFactoryProfile(parallelismSupport).getObject().create())
                .build();

        float weightDifferenceCoefficientFixed = speciationSupport.getWeightDifferenceCoefficient().createFactoryProfile(parallelismSupport).getObject().create();
        float disjointCoefficientFixed = speciationSupport.getDisjointCoefficient().createFactoryProfile(parallelismSupport).getObject().create();
        float excessCoefficientFixed = speciationSupport.getExcessCoefficient().createFactoryProfile(parallelismSupport).getObject().create();
        DualModeSequentialIdFactory speciesIdFactory = new DualModeSequentialIdFactory(parallelismSupport.isEnabled(), "species");
        DualModeGenomePool genomePool = new DualModeGenomePool(parallelismSupport.isEnabled());
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        Pair<RandomSupport> randomSupportPair = ObjectProfile.deconstruct(randomSupportProfile);
        DefaultReproductionTypeFactoryProfile randomReproductionTypeGeneratorProfile = createRandomReproductionTypeGeneratorProfile(parallelismSupport, speciationSupport.getMateOnlyRate(), speciationSupport.getMutateOnlyRate(), randomSupportPair);
        ObjectProfile<SpeciesFitnessStrategy> fitnessStrategyProfile = createFitnessStrategyProfile(parallelismSupport);
        ObjectProfile<SpeciesSelectionStrategyExecutor> evolutionStrategyProfile = createEvolutionStrategyProfile(parallelismSupport);
        ObjectProfile<SpeciesReproductionStrategy> reproductionStrategyProfile = createReproductionStrategyProfile(parallelismSupport);

        return new DefaultContextSpeciationSupport(params, speciesIdFactory, genomePool, genomeCompatibilityCalculator, randomReproductionTypeGeneratorProfile, fitnessStrategyProfile, evolutionStrategyProfile, reproductionStrategyProfile);
    }

    @Override
    public Context.SpeciationParameters params() {
        return params;
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.create().toString();
    }

    @Override
    public void clearSpeciesIds() {
        speciesIdFactory.reset();
    }

    @Override
    public String createGenomeId() {
        return genomePool.createId();
    }

    @Override
    public void clearGenomeIds() {
        genomePool.clearIds();
    }

    @Override
    public Genome createGenesisGenome(final Context context) {
        return genomePool.createGenesis(context);
    }

    @Override
    public double calculateCompatibility(final Genome genome1, final Genome genome2) {
        double compatibility = genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);

        if (compatibility == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }

        if (compatibility == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }

        return compatibility;
    }

    @Override
    public ReproductionType generateReproductionType(final int organisms) {
        return reproductionTypeFactoryProfile.getObject().get(organisms);
    }

    @Override
    public SpeciesFitnessStrategy getFitnessStrategy() {
        return fitnessStrategyProfile.getObject();
    }

    @Override
    public SpeciesSelectionStrategyExecutor getSelectionStrategy() {
        return selectionStrategyProfile.getObject();
    }

    @Override
    public SpeciesReproductionStrategy getReproductionStrategy() {
        return reproductionStrategyProfile.getObject();
    }

    @Override
    public void disposeGenomeId(final Genome genome) {
        genomePool.disposeId(genome);
    }

    @Override
    public int getDisposedGenomeIdCount() {
        return genomePool.getDisposedCount();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("speciation.params", params);
        stateGroup.put("speciation.speciesIdFactory", speciesIdFactory);
        stateGroup.put("speciation.genomePool", genomePool);
        stateGroup.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        stateGroup.put("speciation.reproductionTypeFactoryProfile", reproductionTypeFactoryProfile);
        stateGroup.put("speciation.fitnessStrategyProfile", fitnessStrategyProfile);
        stateGroup.put("speciation.selectionStrategyProfile", selectionStrategyProfile);
        stateGroup.put("speciation.reproductionStrategyProfile", reproductionStrategyProfile);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        params = stateGroup.get("speciation.params");
        speciesIdFactory = DualModeObject.switchMode(stateGroup.get("speciation.speciesIdFactory"), eventLoop != null);
        genomePool = DualModeObject.switchMode(stateGroup.get("speciation.genomePool"), eventLoop != null);
        genomeCompatibilityCalculator = stateGroup.get("speciation.genomeCompatibilityCalculator");
        reproductionTypeFactoryProfile = ObjectProfile.switchProfile(stateGroup.get("speciation.reproductionTypeFactoryProfile"), eventLoop != null);
        fitnessStrategyProfile = ObjectProfile.switchProfile(stateGroup.get("speciation.fitnessStrategyProfile"), eventLoop != null);
        selectionStrategyProfile = ObjectProfile.switchProfile(stateGroup.get("speciation.selectionStrategyProfile"), eventLoop != null);
        reproductionStrategyProfile = ObjectProfile.switchProfile(stateGroup.get("speciation.reproductionStrategyProfile"), eventLoop != null);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultReproductionTypeFactory implements ObjectIndexer<ReproductionType>, Serializable {
        @Serial
        private static final long serialVersionUID = 6788712949153539093L;
        private final RandomSupport randomSupport;
        private final OutputClassifier<ReproductionType> reproductionTypeClassifier;
        private final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int organisms) {
            float value = randomSupport.next();

            if (organisms >= 2) {
                return reproductionTypeClassifier.resolve(value);
            }

            return lessThan2ReproductionTypeClassifier.resolve(value);
        }
    }

    private static final class DefaultReproductionTypeFactoryProfile extends AbstractObjectProfile<ObjectIndexer<ReproductionType>> {
        @Serial
        private static final long serialVersionUID = -976465186511988776L;

        private DefaultReproductionTypeFactoryProfile(final boolean concurrent, final Pair<RandomSupport> randomSupportPair, final OutputClassifier<ReproductionType> reproductionTypeClassifier, final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier) {
            super(concurrent, new DefaultReproductionTypeFactory(randomSupportPair.getLeft(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier), new DefaultReproductionTypeFactory(randomSupportPair.getRight(), reproductionTypeClassifier, lessThan2ReproductionTypeClassifier));
        }
    }
}
