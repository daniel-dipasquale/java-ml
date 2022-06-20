package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.AllFitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.ConcurrentOrganismFitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SharedEnvironmentFitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SharedFitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.GenesisReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.MateAndMutateReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.PreserveMostFitReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness.DualModeFitnessCalculationStrategy;
import com.dipasquale.common.factory.ObjectIndexReader;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectSpeciationSupport implements Context.SpeciationSupport {
    private ContextObjectSpeciationParameters params;
    private DualModeIdFactory speciesIdFactory;
    private DualModeGenomePool genomePool;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private DualModeReproductionTypeFactory reproductionTypeFactory;
    private DualModeFitnessCalculationStrategy fitnessCalculationStrategy;
    private SelectionStrategyExecutor selectionStrategy;
    private ReproductionStrategy reproductionStrategy;

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

    private static DualModeReproductionTypeFactory createReproductionTypeFactory(final InitializationContext initializationContext, final FloatNumber mateOnlyRate, final FloatNumber mutateOnlyRate) {
        float _mateOnlyRate = initializationContext.getFloatSingleton(mateOnlyRate);
        float _mutateOnlyRate = initializationContext.getFloatSingleton(mutateOnlyRate);
        ProbabilityClassifier<ReproductionType> generalReproductionTypeClassifier = createGeneralReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        ProbabilityClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new DualModeReproductionTypeFactory(initializationContext.createDefaultRandomSupport(), generalReproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    private static DualModeFitnessCalculationStrategy createFitnessCalculationStrategy(final InitializationContext initializationContext) {
        return switch (initializationContext.getEnvironmentType()) {
            case ISOLATED -> {
                List<FitnessCalculationStrategy> concurrentStrategies = List.of(
                        new ConcurrentOrganismFitnessCalculationStrategy(),
                        new SharedFitnessCalculationStrategy()
                );

                FitnessCalculationStrategy concurrentStrategy = new FitnessCalculationStrategyController(concurrentStrategies);
                FitnessCalculationStrategy defaultStrategy = new AllFitnessCalculationStrategy();

                yield new DualModeFitnessCalculationStrategy(initializationContext.getConcurrencyLevel(), concurrentStrategy, defaultStrategy);
            }

            case SHARED -> {
                List<FitnessCalculationStrategy> strategies = List.of(
                        new SharedEnvironmentFitnessCalculationStrategy(),
                        new SharedFitnessCalculationStrategy()
                );

                FitnessCalculationStrategy strategy = new FitnessCalculationStrategyController(strategies);

                yield new DualModeFitnessCalculationStrategy(initializationContext.getConcurrencyLevel(), strategy, strategy);
            }
        };
    }

    private static SelectionStrategyExecutor createSelectionStrategy() {
        List<SelectionStrategy> strategies = List.of(
                new LeastFitRemoverSelectionStrategy(),
                new ChampionPromoterSelectionStrategy()
        );

        return new SelectionStrategyExecutor(strategies);
    }

    private static ReproductionStrategy createReproductionStrategy() {
        List<ReproductionStrategy> strategies = List.of(
                new PreserveMostFitReproductionStrategy(),
                new MateAndMutateReproductionStrategy(),
                new GenesisReproductionStrategy()
        );

        return new ReproductionStrategyController(strategies);
    }

    static ContextObjectSpeciationSupport create(final InitializationContext initializationContext, final SpeciationSupport speciationSupport, final GeneralSupport generalSupport) {
        ContextObjectSpeciationParameters params = ContextObjectSpeciationParameters.builder()
                .maximumSpecies(Math.min(initializationContext.getIntegerSingleton(speciationSupport.getMaximumSpecies()), generalSupport.getPopulationSize()))
                .compatibilityThreshold(initializationContext.getFloatSingleton(speciationSupport.getCompatibilityThreshold()))
                .compatibilityThresholdModifier(initializationContext.getFloatSingleton(speciationSupport.getCompatibilityThresholdModifier()))
                .eugenicsThreshold(initializationContext.getFloatSingleton(speciationSupport.getEugenicsThreshold()))
                .elitistThreshold(initializationContext.getFloatSingleton(speciationSupport.getElitistThreshold()))
                .elitistThresholdMinimum(initializationContext.getIntegerSingleton(speciationSupport.getElitistThresholdMinimum()))
                .stagnationDropOffAge(initializationContext.getIntegerSingleton(speciationSupport.getStagnationDropOffAge()))
                .interSpeciesMatingRate(initializationContext.getFloatSingleton(speciationSupport.getInterSpeciesMatingRate()))
                .build();

        float fixedWeightDifferenceCoefficient = initializationContext.getFloatSingleton(speciationSupport.getWeightDifferenceCoefficient());
        float fixedDisjointCoefficient = initializationContext.getFloatSingleton(speciationSupport.getDisjointCoefficient());
        float fixedExcessCoefficient = initializationContext.getFloatSingleton(speciationSupport.getExcessCoefficient());
        DualModeIdFactory speciesIdFactory = new DualModeIdFactory(initializationContext.getConcurrencyLevel(), IdType.SPECIES);
        DualModeGenomePool genomePool = new DualModeGenomePool(initializationContext.getConcurrencyLevel(), initializationContext.createDeque());
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(fixedExcessCoefficient, fixedDisjointCoefficient, fixedWeightDifferenceCoefficient);
        DualModeReproductionTypeFactory reproductionTypeFactory = createReproductionTypeFactory(initializationContext, speciationSupport.getMateOnlyRate(), speciationSupport.getMutateOnlyRate());
        DualModeFitnessCalculationStrategy fitnessCalculationStrategy = createFitnessCalculationStrategy(initializationContext);
        SelectionStrategyExecutor selectionStrategy = createSelectionStrategy();
        ReproductionStrategy reproductionStrategy = createReproductionStrategy();

        return new ContextObjectSpeciationSupport(params, speciesIdFactory, genomePool, genomeCompatibilityCalculator, reproductionTypeFactory, fitnessCalculationStrategy, selectionStrategy, reproductionStrategy);
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

    public FitnessCalculationStrategy getFitnessCalculationStrategy() {
        return fitnessCalculationStrategy;
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

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("speciation.params", params);
        stateGroup.put("speciation.speciesIdFactory", speciesIdFactory);
        stateGroup.put("speciation.genomePool", genomePool);
        stateGroup.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        stateGroup.put("speciation.reproductionTypeFactory", reproductionTypeFactory);
        stateGroup.put("speciation.fitnessCalculationStrategy", fitnessCalculationStrategy);
        stateGroup.put("speciation.selectionStrategy", selectionStrategy);
        stateGroup.put("speciation.reproductionStrategy", reproductionStrategy);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        params = stateGroup.get("speciation.params");
        speciesIdFactory = DualModeObject.activateMode(stateGroup.get("speciation.speciesIdFactory"), concurrencyLevel);
        genomePool = DualModeObject.activateMode(stateGroup.get("speciation.genomePool"), concurrencyLevel);
        genomeCompatibilityCalculator = stateGroup.get("speciation.genomeCompatibilityCalculator");
        reproductionTypeFactory = DualModeObject.activateMode(stateGroup.get("speciation.reproductionTypeFactory"), concurrencyLevel);
        fitnessCalculationStrategy = DualModeObject.activateMode(stateGroup.get("speciation.fitnessCalculationStrategy"), concurrencyLevel);
        selectionStrategy = stateGroup.get("speciation.selectionStrategy");
        reproductionStrategy = stateGroup.get("speciation.reproductionStrategy");
    }

    public void load(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeReproductionTypeFactory implements ObjectIndexReader<ReproductionType>, DualModeObject, Serializable {
        @Serial
        private static final long serialVersionUID = 6788712949153539093L;
        private final DualModeRandomSupport randomSupport;
        private final ProbabilityClassifier<ReproductionType> generalReproductionTypeClassifier;
        private final ProbabilityClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int organisms) {
            float value = randomSupport.next();

            if (organisms >= 2) {
                return generalReproductionTypeClassifier.get(value);
            }

            return lessThan2ReproductionTypeClassifier.get(value);
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            randomSupport.activateMode(concurrencyLevel);
        }
    }
}
