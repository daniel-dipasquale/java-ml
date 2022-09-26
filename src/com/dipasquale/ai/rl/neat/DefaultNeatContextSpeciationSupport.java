package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.PopulationSizeProvider;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalFloatFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalGenomeCompatibilityCalculatorFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalIntegerFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalMinIntegerFactory;
import com.dipasquale.ai.rl.neat.generational.factory.GenerationalReproductionTypeProvider;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomePool;
import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.AllFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.CommunalOnlyFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.ConcurrentOrganismFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.GenesisReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.MateAndMutateReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.PreserveMostFitReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategyController;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionPromoterSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.LeastFitRemoverSelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategyExecutor;
import com.dipasquale.data.structure.collection.ListSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class DefaultNeatContextSpeciationSupport implements NeatContext.SpeciationSupport {
    private final NeatEnvironmentType environmentType;
    private final PopulationSizeProvider populationSize;
    private final GenerationalMinIntegerFactory maximumSpecies;
    private final IdFactory speciesIdFactory;
    private final GenomePool genomePool;
    private final GenerationalFloatFactory compatibilityThreshold;
    private final GenerationalFloatFactory compatibilityThresholdModifier;
    private final GenerationalGenomeCompatibilityCalculatorFactory genomeCompatibilityCalculator;
    private final GenerationalFloatFactory eugenicsThreshold;
    private final GenerationalFloatFactory elitistThreshold;
    private final GenerationalIntegerFactory minimumElitistDesired;
    private final GenerationalReproductionTypeProvider reproductionTypeProvider;
    private final GenerationalIntegerFactory stagnationDropOffAge;
    private final GenerationalFloatFactory interSpeciesMatingRate;
    @Getter
    private final FitnessEvaluationStrategy fitnessEvaluationStrategy;
    @Getter
    private final SelectionStrategyExecutor selectionStrategy;
    @Getter
    private final ReproductionStrategy reproductionStrategy;

    private static FitnessEvaluationStrategy createFitnessEvaluationStrategy(final NeatEnvironmentType environmentType, final int concurrencyLevel) {
        return switch (environmentType) {
            case SECLUDED -> {
                if (concurrencyLevel == 0) {
                    yield AllFitnessEvaluationStrategy.getInstance();
                }

                List<FitnessEvaluationStrategy> concurrentStrategies = ListSupport.<FitnessEvaluationStrategy>builder()
                        .add(ConcurrentOrganismFitnessEvaluationStrategy.getInstance())
                        .add(SpeciesFitnessEvaluationStrategy.getInstance())
                        .build();

                yield new FitnessEvaluationStrategyController(concurrentStrategies);
            }

            case COMMUNAL -> {
                List<FitnessEvaluationStrategy> strategies = ListSupport.<FitnessEvaluationStrategy>builder()
                        .add(CommunalOnlyFitnessEvaluationStrategy.getInstance())
                        .add(SpeciesFitnessEvaluationStrategy.getInstance())
                        .build();

                yield new FitnessEvaluationStrategyController(strategies);
            }
        };
    }

    private static SelectionStrategyExecutor createSelectionStrategy() {
        List<SelectionStrategy> strategies = ListSupport.<SelectionStrategy>builder()
                .add(LeastFitRemoverSelectionStrategy.getInstance())
                .add(ChampionPromoterSelectionStrategy.getInstance())
                .build();

        return new SelectionStrategyExecutor(strategies);
    }

    private static ReproductionStrategy createReproductionStrategy() {
        List<ReproductionStrategy> strategies = ListSupport.<ReproductionStrategy>builder()
                .add(PreserveMostFitReproductionStrategy.getInstance())
                .add(MateAndMutateReproductionStrategy.getInstance())
                .add(GenesisReproductionStrategy.getInstance())
                .build();

        return new ReproductionStrategyController(strategies);
    }

    DefaultNeatContextSpeciationSupport(final NeatEnvironmentType environmentType, final PopulationSizeProvider populationSize, final GenerationalMinIntegerFactory maximumSpecies, final IdFactory speciesIdFactory, final GenomePool genomePool, final GenerationalFloatFactory compatibilityThreshold, final GenerationalFloatFactory compatibilityThresholdModifier, final GenerationalGenomeCompatibilityCalculatorFactory genomeCompatibilityCalculator, final GenerationalFloatFactory eugenicsThreshold, final GenerationalFloatFactory elitistThreshold, final GenerationalIntegerFactory minimumElitistDesired, final GenerationalReproductionTypeProvider reproductionTypeProvider, final GenerationalIntegerFactory stagnationDropOffAge, final GenerationalFloatFactory interSpeciesMatingRate, final int concurrencyLevel) {
        this.environmentType = environmentType;
        this.populationSize = populationSize;
        this.maximumSpecies = maximumSpecies;
        this.speciesIdFactory = speciesIdFactory;
        this.genomePool = genomePool;
        this.compatibilityThreshold = compatibilityThreshold;
        this.compatibilityThresholdModifier = compatibilityThresholdModifier;
        this.genomeCompatibilityCalculator = genomeCompatibilityCalculator;
        this.eugenicsThreshold = eugenicsThreshold;
        this.elitistThreshold = elitistThreshold;
        this.minimumElitistDesired = minimumElitistDesired;
        this.reproductionTypeProvider = reproductionTypeProvider;
        this.stagnationDropOffAge = stagnationDropOffAge;
        this.interSpeciesMatingRate = interSpeciesMatingRate;
        this.fitnessEvaluationStrategy = createFitnessEvaluationStrategy(environmentType, concurrencyLevel);
        this.selectionStrategy = createSelectionStrategy();
        this.reproductionStrategy = createReproductionStrategy();
    }

    @Override
    public int getPopulationSize() {
        return populationSize.getValue();
    }

    @Override
    public int getMaximumSpecies() {
        return maximumSpecies.getValue();
    }

    @Override
    public String createSpeciesId() {
        return speciesIdFactory.create().toString();
    }

    @Override
    public int createGenomeId() {
        return genomePool.createGenomeId();
    }

    @Override
    public Genome createGenesisGenome(final NeatContext context) {
        return genomePool.createGenesisGenome(context);
    }

    @Override
    public float calculateCompatibilityThreshold(final int generation) {
        return compatibilityThreshold.getValue() * (float) Math.pow(compatibilityThresholdModifier.getValue(), generation);
    }

    @Override
    public float calculateCompatibility(final Genome genome1, final Genome genome2) {
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public float getEugenicsThreshold() {
        return eugenicsThreshold.getValue();
    }

    @Override
    public float getElitistThreshold() {
        return elitistThreshold.getValue();
    }

    @Override
    public int getMinimumElitistDesired() {
        return minimumElitistDesired.getValue();
    }

    @Override
    public ReproductionType generateReproductionType(final int organisms) {
        return reproductionTypeProvider.provide(organisms);
    }

    @Override
    public int getStagnationDropOffAge() {
        return stagnationDropOffAge.getValue();
    }

    @Override
    public float getInterSpeciesMatingRate() {
        return interSpeciesMatingRate.getValue();
    }

    @Override
    public void disposeGenomeId(final Genome genome) {
        genomePool.disposeId(genome);
    }

    @Override
    public int getDisposedGenomeIdCount() {
        return genomePool.getDisposedGenomeIdCount();
    }

    @Override
    public int advanceGeneration(final int size) {
        populationSize.reinitialize(size);
        maximumSpecies.reinitialize();
        compatibilityThreshold.reinitialize();
        compatibilityThresholdModifier.reinitialize();
        genomeCompatibilityCalculator.reinitialize();
        eugenicsThreshold.reinitialize();
        elitistThreshold.reinitialize();
        minimumElitistDesired.reinitialize();
        reproductionTypeProvider.reinitialize();
        stagnationDropOffAge.reinitialize();
        interSpeciesMatingRate.reinitialize();

        return populationSize.getValue();
    }

    @Override
    public void clear() {
        speciesIdFactory.reset();
        genomePool.clearGenomeIds();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("speciation.environmentType", environmentType);
        stateGroup.put("speciation.populationSize", populationSize);
        stateGroup.put("speciation.maximumSpecies", maximumSpecies);
        stateGroup.put("speciation.speciesIdFactory", speciesIdFactory);
        stateGroup.put("speciation.genomePool", genomePool);
        stateGroup.put("speciation.compatibilityThreshold", compatibilityThreshold);
        stateGroup.put("speciation.compatibilityThresholdModifier", compatibilityThresholdModifier);
        stateGroup.put("speciation.genomeCompatibilityCalculator", genomeCompatibilityCalculator);
        stateGroup.put("speciation.eugenicsThreshold", eugenicsThreshold);
        stateGroup.put("speciation.elitistThreshold", elitistThreshold);
        stateGroup.put("speciation.minimumElitistDesired", minimumElitistDesired);
        stateGroup.put("speciation.reproductionTypeProvider", reproductionTypeProvider);
        stateGroup.put("speciation.stagnationDropOffAge", stagnationDropOffAge);
        stateGroup.put("speciation.interSpeciesMatingRate", interSpeciesMatingRate);
    }

    static DefaultNeatContextSpeciationSupport create(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop) {
        NeatEnvironmentType environmentType = stateGroup.get("speciation.environmentType");
        PopulationSizeProvider populationSize = stateGroup.get("speciation.populationSize");
        GenerationalMinIntegerFactory maximumSpecies = stateGroup.get("speciation.maximumSpecies");
        IdFactory speciesIdFactory = stateGroup.get("speciation.speciesIdFactory");
        GenomePool genomePool = stateGroup.get("speciation.genomePool");
        GenerationalFloatFactory compatibilityThreshold = stateGroup.get("speciation.compatibilityThreshold");
        GenerationalFloatFactory compatibilityThresholdModifier = stateGroup.get("speciation.compatibilityThresholdModifier");
        GenerationalGenomeCompatibilityCalculatorFactory genomeCompatibilityCalculator = stateGroup.get("speciation.genomeCompatibilityCalculator");
        GenerationalFloatFactory eugenicsThreshold = stateGroup.get("speciation.eugenicsThreshold");
        GenerationalFloatFactory elitistThreshold = stateGroup.get("speciation.elitistThreshold");
        GenerationalIntegerFactory minimumElitistDesired = stateGroup.get("speciation.minimumElitistDesired");
        GenerationalReproductionTypeProvider reproductionTypeProvider = stateGroup.get("speciation.reproductionTypeProvider");
        GenerationalIntegerFactory stagnationDropOffAge = stateGroup.get("speciation.stagnationDropOffAge");
        GenerationalFloatFactory interSpeciesMatingRate = stateGroup.get("speciation.interSpeciesMatingRate");
        FitnessEvaluationStrategy fitnessEvaluationStrategy = createFitnessEvaluationStrategy(environmentType, ParallelismSettings.extractThreadIds(eventLoop).size());
        SelectionStrategyExecutor selectionStrategy = createSelectionStrategy();
        ReproductionStrategy reproductionStrategy = createReproductionStrategy();

        return new DefaultNeatContextSpeciationSupport(environmentType, populationSize, maximumSpecies, speciesIdFactory, genomePool, compatibilityThreshold, compatibilityThresholdModifier, genomeCompatibilityCalculator, eugenicsThreshold, elitistThreshold, minimumElitistDesired, reproductionTypeProvider, stagnationDropOffAge, interSpeciesMatingRate, fitnessEvaluationStrategy, selectionStrategy, reproductionStrategy);
    }
}
