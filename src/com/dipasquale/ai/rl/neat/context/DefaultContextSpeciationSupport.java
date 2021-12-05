package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.core.SpeciationSupport;
import com.dipasquale.ai.rl.neat.genotype.Genome;
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
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness.DualModeSpeciesFitnessStrategy;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private DualModeIdFactory speciesIdFactory;
    private DualModeGenomePool genomePool;
    private GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private DualModeReproductionTypeFactory reproductionTypeFactory;
    private DualModeSpeciesFitnessStrategy fitnessStrategy;
    private SpeciesSelectionStrategyExecutor selectionStrategy;
    private SpeciesReproductionStrategy reproductionStrategy;

    private static OutputClassifier<ReproductionType> createGeneralReproductionTypeClassifier(final float mateOnlyRate, final float mutateOnlyRate) {
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

    private static DualModeReproductionTypeFactory createReproductionTypeFactory(final InitializationContext initializationContext, final FloatNumber mateOnlyRate, final FloatNumber mutateOnlyRate) {
        float _mateOnlyRate = mateOnlyRate.getSingletonValue(initializationContext);
        float _mutateOnlyRate = mutateOnlyRate.getSingletonValue(initializationContext);
        OutputClassifier<ReproductionType> generalReproductionTypeClassifier = createGeneralReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new DualModeReproductionTypeFactory(initializationContext.getRandomSupport(), generalReproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    private static DualModeSpeciesFitnessStrategy createFitnessStrategy(final InitializationContext initializationContext) {
        List<SpeciesFitnessStrategy> concurrentStrategies = List.of(
                new ParallelUpdateSpeciesFitnessStrategy(),
                new UpdateSharedSpeciesFitnessStrategy()
        );

        SpeciesFitnessStrategy concurrentStrategy = new MultiSpeciesFitnessStrategy(concurrentStrategies);
        SpeciesFitnessStrategy defaultStrategy = new UpdateAllFitnessSpeciesFitnessStrategy();

        return new DualModeSpeciesFitnessStrategy(initializationContext.getParallelism().getConcurrencyLevel(), concurrentStrategy, defaultStrategy);
    }

    private static SpeciesSelectionStrategyExecutor createSelectionStrategy() {
        List<SpeciesSelectionStrategy> strategies = List.of(
                new LeastFitRemoverSpeciesSelectionStrategy(),
                new ChampionPromoterSelectionStrategy()
        );

        return new SpeciesSelectionStrategyExecutor(strategies);
    }

    private static SpeciesReproductionStrategy createReproductionStrategy() {
        List<SpeciesReproductionStrategy> strategies = List.of(
                new PreserveMostFitSpeciesReproductionStrategy(),
                new MateAndMutateSpeciesReproductionStrategy(),
                new GenesisSpeciesReproductionStrategy()
        );

        return new MultiSpeciesReproductionStrategy(strategies);
    }

    public static DefaultContextSpeciationSupport create(final InitializationContext initializationContext, final SpeciationSupport speciationSupport, final GeneralEvaluatorSupport generalEvaluatorSupport) {
        DefaultContextSpeciationParameters params = DefaultContextSpeciationParameters.builder()
                .maximumSpecies(Math.min(speciationSupport.getMaximumSpecies().getSingletonValue(initializationContext), generalEvaluatorSupport.getPopulationSize().getSingletonValue(initializationContext)))
                .compatibilityThreshold(speciationSupport.getCompatibilityThreshold().getSingletonValue(initializationContext))
                .compatibilityThresholdModifier(speciationSupport.getCompatibilityThresholdModifier().getSingletonValue(initializationContext))
                .eugenicsThreshold(speciationSupport.getEugenicsThreshold().getSingletonValue(initializationContext))
                .elitistThreshold(speciationSupport.getElitistThreshold().getSingletonValue(initializationContext))
                .elitistThresholdMinimum(speciationSupport.getElitistThresholdMinimum().getSingletonValue(initializationContext))
                .stagnationDropOffAge(speciationSupport.getStagnationDropOffAge().getSingletonValue(initializationContext))
                .interSpeciesMatingRate(speciationSupport.getInterSpeciesMatingRate().getSingletonValue(initializationContext))
                .build();

        float weightDifferenceCoefficientFixed = speciationSupport.getWeightDifferenceCoefficient().getSingletonValue(initializationContext);
        float disjointCoefficientFixed = speciationSupport.getDisjointCoefficient().getSingletonValue(initializationContext);
        float excessCoefficientFixed = speciationSupport.getExcessCoefficient().getSingletonValue(initializationContext);
        DualModeIdFactory speciesIdFactory = new DualModeIdFactory(initializationContext.getParallelism().getConcurrencyLevel(), "species");
        DualModeGenomePool genomePool = new DualModeGenomePool(initializationContext.getParallelism().getDequeFactory());
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        DualModeReproductionTypeFactory reproductionTypeFactory = createReproductionTypeFactory(initializationContext, speciationSupport.getMateOnlyRate(), speciationSupport.getMutateOnlyRate());
        DualModeSpeciesFitnessStrategy fitnessStrategy = createFitnessStrategy(initializationContext);
        SpeciesSelectionStrategyExecutor selectionStrategy = createSelectionStrategy();
        SpeciesReproductionStrategy reproductionStrategy = createReproductionStrategy();

        return new DefaultContextSpeciationSupport(params, speciesIdFactory, genomePool, genomeCompatibilityCalculator, reproductionTypeFactory, fitnessStrategy, selectionStrategy, reproductionStrategy);
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
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public ReproductionType generateReproductionType(final int organisms) {
        return reproductionTypeFactory.get(organisms);
    }

    @Override
    public SpeciesFitnessStrategy getFitnessStrategy() {
        return fitnessStrategy;
    }

    @Override
    public SpeciesSelectionStrategyExecutor getSelectionStrategy() {
        return selectionStrategy;
    }

    @Override
    public SpeciesReproductionStrategy getReproductionStrategy() {
        return reproductionStrategy;
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
        stateGroup.put("speciation.reproductionTypeFactory", reproductionTypeFactory);
        stateGroup.put("speciation.fitnessStrategy", fitnessStrategy);
        stateGroup.put("speciation.selectionStrategy", selectionStrategy);
        stateGroup.put("speciation.reproductionStrategy", reproductionStrategy);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        params = stateGroup.get("speciation.params");
        speciesIdFactory = DualModeObject.activateMode(stateGroup.get("speciation.speciesIdFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        genomePool = DualModeObject.activateMode(stateGroup.get("speciation.genomePool"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        genomeCompatibilityCalculator = stateGroup.get("speciation.genomeCompatibilityCalculator");
        reproductionTypeFactory = DualModeObject.activateMode(stateGroup.get("speciation.reproductionTypeFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        fitnessStrategy = DualModeObject.activateMode(stateGroup.get("speciation.fitnessStrategy"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        selectionStrategy = stateGroup.get("speciation.selectionStrategy");
        reproductionStrategy = stateGroup.get("speciation.reproductionStrategy");
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DualModeReproductionTypeFactory implements ObjectIndexer<ReproductionType>, DualModeObject, Serializable {
        @Serial
        private static final long serialVersionUID = 6788712949153539093L;
        private final DualModeRandomSupport randomSupport;
        private final OutputClassifier<ReproductionType> generalReproductionTypeClassifier;
        private final OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier;

        @Override
        public ReproductionType get(final int organisms) {
            float value = randomSupport.next();

            if (organisms >= 2) {
                return generalReproductionTypeClassifier.resolve(value);
            }

            return lessThan2ReproductionTypeClassifier.resolve(value);
        }

        @Override
        public int concurrencyLevel() {
            return randomSupport.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            randomSupport.activateMode(concurrencyLevel);
        }
    }
}
