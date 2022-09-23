package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.genotype.GenomePool;
import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.AllFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.ConcurrentOrganismFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SharedEnvironmentFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SharedFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.GenesisReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.MateAndMutateReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.PreserveMostFitReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategyExecutor;
import com.dipasquale.common.factory.ObjectIndexReader;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.data.structure.collection.ListSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectSpeciationSupport implements Context.SpeciationSupport {
    private final ContextObjectSpeciationParameters params;
    private final NeatEnvironmentType environmentType;
    private final IdFactory speciesIdFactory;
    private final GenomePool genomePool;
    private final GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private final ReproductionTypeFactory reproductionTypeFactory;
    private final FitnessEvaluationStrategy fitnessEvaluationStrategy;
    private final SelectionStrategyExecutor selectionStrategy;
    private final ReproductionStrategy reproductionStrategy;

    private static ProbabilityClassifier<ReproductionType> createGeneralReproductionTypeClassifier(final float mateOnlyRate, final float mutateOnlyRate) {
        float totalRate = mateOnlyRate * 2f + mutateOnlyRate * 2f;
        ProbabilityClassifier<ReproductionType> reproductionTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(totalRate, 0f) > 0) {
            reproductionTypeClassifier.add(mateOnlyRate / totalRate, ReproductionType.MATE_ONLY);
            reproductionTypeClassifier.add(mutateOnlyRate / totalRate, ReproductionType.MUTATE_ONLY);
            reproductionTypeClassifier.add(1f - (mateOnlyRate + mutateOnlyRate) / totalRate, ReproductionType.MATE_AND_MUTATE);
        } else {
            reproductionTypeClassifier.add(1f, ReproductionType.MATE_AND_MUTATE);
        }

        return reproductionTypeClassifier;
    }

    private static ProbabilityClassifier<ReproductionType> createLessThan2ReproductionTypeClassifier(final float mutateOnlyRate) {
        ProbabilityClassifier<ReproductionType> reproductionTypeClassifier = new ProbabilityClassifier<>();

        if (Float.compare(mutateOnlyRate, 0f) > 0) {
            reproductionTypeClassifier.add(1f, ReproductionType.MUTATE_ONLY);
        } else {
            reproductionTypeClassifier.add(1f, ReproductionType.CLONE);
        }

        return reproductionTypeClassifier;
    }

    private static ReproductionTypeFactory createReproductionTypeFactory(final InitializationContext initializationContext, final FloatNumber mateOnlyRate, final FloatNumber mutateOnlyRate) {
        float _mateOnlyRate = initializationContext.provideSingleton(mateOnlyRate);
        float _mutateOnlyRate = initializationContext.provideSingleton(mutateOnlyRate);
        ProbabilityClassifier<ReproductionType> generalReproductionTypeClassifier = createGeneralReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        ProbabilityClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new ReproductionTypeFactory(initializationContext.createDefaultRandomSupport(), generalReproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    private static FitnessEvaluationStrategy createFitnessEvaluationStrategy(final NeatEnvironmentType environmentType, final int concurrencyLevel) {
        return switch (environmentType) {
            case SECLUDED -> {
                if (concurrencyLevel == 0) {
                    yield new AllFitnessEvaluationStrategy();
                }

                List<FitnessEvaluationStrategy> concurrentStrategies = ListSupport.<FitnessEvaluationStrategy>builder()
                        .add(new ConcurrentOrganismFitnessEvaluationStrategy())
                        .add(new SharedFitnessEvaluationStrategy())
                        .build();

                yield new FitnessEvaluationStrategyController(concurrentStrategies);
            }

            case COMMUNAL -> {
                List<FitnessEvaluationStrategy> strategies = ListSupport.<FitnessEvaluationStrategy>builder()
                        .add(new SharedEnvironmentFitnessEvaluationStrategy())
                        .add(new SharedFitnessEvaluationStrategy())
                        .build();

                yield new FitnessEvaluationStrategyController(strategies);
            }
        };
    }

    private static FitnessEvaluationStrategy createFitnessEvaluationStrategy(final InitializationContext initializationContext) {
        return createFitnessEvaluationStrategy(initializationContext.getEnvironmentType(), initializationContext.getThreadIds().size());
    }

    private static SelectionStrategyExecutor createSelectionStrategy() {
        List<SelectionStrategy> strategies = ListSupport.<SelectionStrategy>builder()
                .add(new LeastFitRemoverSelectionStrategy())
                .add(new ChampionPromoterSelectionStrategy())
                .build();

        return new SelectionStrategyExecutor(strategies);
    }

    private static ReproductionStrategy createReproductionStrategy() {
        List<ReproductionStrategy> strategies = ListSupport.<ReproductionStrategy>builder()
                .add(new PreserveMostFitReproductionStrategy())
                .add(new MateAndMutateReproductionStrategy())
                .add(new GenesisReproductionStrategy())
                .build();

        return new ReproductionStrategyController(strategies);
    }

    static ContextObjectSpeciationSupport create(final InitializationContext initializationContext, final SpeciationSettings speciationSettings, final GeneralSettings generalSettings) {
        ContextObjectSpeciationParameters params = ContextObjectSpeciationParameters.builder()
                .maximumSpecies(Math.min(initializationContext.provideSingleton(speciationSettings.getMaximumSpecies()), generalSettings.getPopulationSize()))
                .compatibilityThreshold(initializationContext.provideSingleton(speciationSettings.getCompatibilityThreshold()))
                .compatibilityThresholdModifier(initializationContext.provideSingleton(speciationSettings.getCompatibilityThresholdModifier()))
                .eugenicsThreshold(initializationContext.provideSingleton(speciationSettings.getEugenicsThreshold()))
                .elitistThreshold(initializationContext.provideSingleton(speciationSettings.getElitistThreshold()))
                .elitistThresholdMinimum(initializationContext.provideSingleton(speciationSettings.getElitistThresholdMinimum()))
                .stagnationDropOffAge(initializationContext.provideSingleton(speciationSettings.getStagnationDropOffAge()))
                .interSpeciesMatingRate(initializationContext.provideSingleton(speciationSettings.getInterSpeciesMatingRate()))
                .build();

        NeatEnvironmentType environmentType = initializationContext.getEnvironmentType();
        float fixedWeightDifferenceCoefficient = initializationContext.provideSingleton(speciationSettings.getWeightDifferenceCoefficient());
        float fixedDisjointCoefficient = initializationContext.provideSingleton(speciationSettings.getDisjointCoefficient());
        float fixedExcessCoefficient = initializationContext.provideSingleton(speciationSettings.getExcessCoefficient());
        IdFactory speciesIdFactory = new IdFactory(IdType.SPECIES);
        GenomePool genomePool = new GenomePool();
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(fixedExcessCoefficient, fixedDisjointCoefficient, fixedWeightDifferenceCoefficient);
        ReproductionTypeFactory reproductionTypeFactory = createReproductionTypeFactory(initializationContext, speciationSettings.getMateOnlyRate(), speciationSettings.getMutateOnlyRate());
        FitnessEvaluationStrategy fitnessEvaluationStrategy = createFitnessEvaluationStrategy(initializationContext);
        SelectionStrategyExecutor selectionStrategy = createSelectionStrategy();
        ReproductionStrategy reproductionStrategy = createReproductionStrategy();

        return new ContextObjectSpeciationSupport(params, environmentType, speciesIdFactory, genomePool, genomeCompatibilityCalculator, reproductionTypeFactory, fitnessEvaluationStrategy, selectionStrategy, reproductionStrategy);
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
    public int createGenomeId() {
        return genomePool.createGenomeId();
    }

    @Override
    public void clearGenomeIds() {
        genomePool.clearGenomeIds();
    }

    @Override
    public Genome createGenesisGenome(final Context context) {
        return genomePool.createGenesisGenome(context);
    }

    @Override
    public float calculateCompatibility(final Genome genome1, final Genome genome2) {
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public ReproductionType generateReproductionType(final int organisms) {
        return reproductionTypeFactory.get(organisms);
    }

    @Override
    public FitnessEvaluationStrategy getFitnessEvaluationStrategy() {
        return fitnessEvaluationStrategy;
    }

    @Override
    public SelectionStrategyExecutor getSelectionStrategy() {
        return selectionStrategy;
    }

    @Override
    public ReproductionStrategy getReproductionStrategy() {
        return reproductionStrategy;
    }

    @Override
    public void disposeGenomeId(final Genome genome) {
        genomePool.disposeId(genome);
    }

    @Override
    public int getDisposedGenomeIdCount() {
        return genomePool.getDisposedGenomeIdCount();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("speciation.params", params);
        stateGroup.put("speciation.environmentType", environmentType);
        stateGroup.put("speciation.speciesIdFactory", speciesIdFactory);
        stateGroup.put("speciation.genomePool", genomePool);
        stateGroup.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        stateGroup.put("speciation.reproductionTypeFactory", reproductionTypeFactory);
        stateGroup.put("speciation.selectionStrategy", selectionStrategy);
        stateGroup.put("speciation.reproductionStrategy", reproductionStrategy);
    }

    static ContextObjectSpeciationSupport create(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop) {
        ContextObjectSpeciationParameters params = stateGroup.get("speciation.params");
        NeatEnvironmentType environmentType = stateGroup.get("speciation.environmentType");
        IdFactory speciesIdFactory = stateGroup.get("speciation.speciesIdFactory");
        GenomePool genomePool = stateGroup.get("speciation.genomePool");
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = stateGroup.get("speciation.genomeCompatibilityCalculator");
        ReproductionTypeFactory reproductionTypeFactory = stateGroup.get("speciation.reproductionTypeFactory");
        FitnessEvaluationStrategy fitnessEvaluationStrategy = createFitnessEvaluationStrategy(environmentType, ParallelismSettings.getThreadIds(eventLoop).size());
        SelectionStrategyExecutor selectionStrategy = stateGroup.get("speciation.selectionStrategy");
        ReproductionStrategy reproductionStrategy = stateGroup.get("speciation.reproductionStrategy");

        return new ContextObjectSpeciationSupport(params, environmentType, speciesIdFactory, genomePool, genomeCompatibilityCalculator, reproductionTypeFactory, fitnessEvaluationStrategy, selectionStrategy, reproductionStrategy);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReproductionTypeFactory implements ObjectIndexReader<ReproductionType>, Serializable {
        @Serial
        private static final long serialVersionUID = 6788712949153539093L;
        private final RandomSupport randomSupport;
        private final ProbabilityClassifier<ReproductionType> generalReproductionTypeClassifier;
        private final ProbabilityClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int organisms) {
            float value = randomSupport.nextFloat();

            if (organisms >= 2) {
                return generalReproductionTypeClassifier.get(value);
            }

            return lessThan2ReproductionTypeClassifier.get(value);
        }
    }
}
