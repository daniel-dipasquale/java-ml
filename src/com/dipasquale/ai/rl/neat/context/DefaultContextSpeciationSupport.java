package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeCompatibilityCalculator;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
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
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeGenomePool;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.strategy.fitness.DualModeSpeciesFitnessStrategy;
import com.dipasquale.common.factory.ObjectIndexer;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextSpeciationSupport implements Context.SpeciationSupport {
    private DefaultContextSpeciationParameters params;
    private DualModeSequentialIdFactory speciesIdFactory;
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

    private static DualModeReproductionTypeFactory createReproductionTypeFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final FloatNumber mateOnlyRate, final FloatNumber mutateOnlyRate) {
        float _mateOnlyRate = mateOnlyRate.getSingletonValue(parallelismSupport, randomSupports);
        float _mutateOnlyRate = mutateOnlyRate.getSingletonValue(parallelismSupport, randomSupports);
        OutputClassifier<ReproductionType> generalReproductionTypeClassifier = createGeneralReproductionTypeClassifier(_mateOnlyRate, _mutateOnlyRate);
        OutputClassifier<ReproductionType> lessThan2ReproductionTypeClassifier = createLessThan2ReproductionTypeClassifier(_mutateOnlyRate);

        return new DualModeReproductionTypeFactory(randomSupport, generalReproductionTypeClassifier, lessThan2ReproductionTypeClassifier);
    }

    private static DualModeSpeciesFitnessStrategy createFitnessStrategy(final ParallelismSupport parallelismSupport) {
        List<SpeciesFitnessStrategy> concurrentStrategies = ImmutableList.<SpeciesFitnessStrategy>builder()
                .add(new ParallelUpdateSpeciesFitnessStrategy())
                .add(new UpdateSharedSpeciesFitnessStrategy())
                .build();

        SpeciesFitnessStrategy concurrentStrategy = new MultiSpeciesFitnessStrategy(concurrentStrategies);
        SpeciesFitnessStrategy defaultStrategy = new UpdateAllFitnessSpeciesFitnessStrategy();

        return new DualModeSpeciesFitnessStrategy(parallelismSupport.getConcurrencyLevel(), concurrentStrategy, defaultStrategy);
    }

    private static SpeciesSelectionStrategyExecutor createSelectionStrategy() {
        List<SpeciesSelectionStrategy> strategies = ImmutableList.<SpeciesSelectionStrategy>builder()
                .add(new LeastFitRemoverSpeciesSelectionStrategy())
                .add(new ChampionPromoterSelectionStrategy())
                .build();

        return new SpeciesSelectionStrategyExecutor(strategies);
    }

    private static SpeciesReproductionStrategy createReproductionStrategy() {
        List<SpeciesReproductionStrategy> strategies = ImmutableList.<SpeciesReproductionStrategy>builder()
                .add(new PreserveMostFitSpeciesReproductionStrategy())
                .add(new MateAndMutateSpeciesReproductionStrategy())
                .add(new GenesisSpeciesReproductionStrategy())
                .build();

        return new MultiSpeciesReproductionStrategy(strategies);
    }

    public static DefaultContextSpeciationSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final SpeciationSupport speciationSupport, final GeneralEvaluatorSupport generalEvaluatorSupport) {
        DefaultContextSpeciationParameters params = DefaultContextSpeciationParameters.builder()
                .maximumSpecies(Math.min(speciationSupport.getMaximumSpecies().getSingletonValue(parallelismSupport, randomSupports), generalEvaluatorSupport.getPopulationSize().getSingletonValue(parallelismSupport, randomSupports)))
                .compatibilityThreshold(speciationSupport.getCompatibilityThreshold().getSingletonValue(parallelismSupport, randomSupports))
                .compatibilityThresholdModifier(speciationSupport.getCompatibilityThresholdModifier().getSingletonValue(parallelismSupport, randomSupports))
                .eugenicsThreshold(speciationSupport.getEugenicsThreshold().getSingletonValue(parallelismSupport, randomSupports))
                .elitistThreshold(speciationSupport.getElitistThreshold().getSingletonValue(parallelismSupport, randomSupports))
                .elitistThresholdMinimum(speciationSupport.getElitistThresholdMinimum().getSingletonValue(parallelismSupport, randomSupports))
                .stagnationDropOffAge(speciationSupport.getStagnationDropOffAge().getSingletonValue(parallelismSupport, randomSupports))
                .interSpeciesMatingRate(speciationSupport.getInterSpeciesMatingRate().getSingletonValue(parallelismSupport, randomSupports))
                .build();

        float weightDifferenceCoefficientFixed = speciationSupport.getWeightDifferenceCoefficient().getSingletonValue(parallelismSupport, randomSupports);
        float disjointCoefficientFixed = speciationSupport.getDisjointCoefficient().getSingletonValue(parallelismSupport, randomSupports);
        float excessCoefficientFixed = speciationSupport.getExcessCoefficient().getSingletonValue(parallelismSupport, randomSupports);
        DualModeSequentialIdFactory speciesIdFactory = new DualModeSequentialIdFactory(parallelismSupport.getConcurrencyLevel(), "species");
        DualModeGenomePool genomePool = new DualModeGenomePool(parallelismSupport.getDequeFactory());
        GenomeCompatibilityCalculator genomeCompatibilityCalculator = new GenomeCompatibilityCalculator(excessCoefficientFixed, disjointCoefficientFixed, weightDifferenceCoefficientFixed);
        DualModeReproductionTypeFactory reproductionTypeFactory = createReproductionTypeFactory(parallelismSupport, randomSupports, randomSupport, speciationSupport.getMateOnlyRate(), speciationSupport.getMutateOnlyRate());
        DualModeSpeciesFitnessStrategy fitnessStrategy = createFitnessStrategy(parallelismSupport);
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
