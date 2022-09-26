package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.FitnessBucketProvider;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivatorPool;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class DefaultNeatContextActivationSupport implements NeatContext.ActivationSupport {
    private final GenomeActivatorPool genomeActivatorPool;
    private final StandardSecludedNeatEnvironment standardSecludedEnvironment;
    private final StandardCommunalNeatEnvironment standardCommunalEnvironment;
    private final HistoricalMarkings historicalMarkings;

    DefaultNeatContextActivationSupport(final GenomeActivatorPool genomeActivatorPool, final SecludedNeatEnvironment environment, final FitnessBucketProvider fitnessBucketProvider, final HistoricalMarkings historicalMarkings) {
        this(genomeActivatorPool, new StandardSecludedNeatEnvironment(environment, fitnessBucketProvider), null, historicalMarkings);
    }

    DefaultNeatContextActivationSupport(final GenomeActivatorPool genomeActivatorPool, final CommunalNeatEnvironment environment, final FitnessBucketProvider fitnessBucketProvider, final HistoricalMarkings historicalMarkings) {
        this(genomeActivatorPool, null, new StandardCommunalNeatEnvironment(environment, fitnessBucketProvider), historicalMarkings);
    }

    @Override
    public void initialize(final int populationSize) {
        genomeActivatorPool.expandIfInsufficient(populationSize);

        if (standardSecludedEnvironment != null) {
            standardSecludedEnvironment.expandIfInsufficient(populationSize);
        }

        if (standardCommunalEnvironment != null) {
            standardCommunalEnvironment.expandIfInsufficient(populationSize);
        }
    }

    @Override
    public GenomeActivator provideActivator(final Genome genome, final PopulationState populationState, final NeatContext.GenomeActivatorType type) {
        return switch (type) {
            case PERSISTENT -> genomeActivatorPool.provide(genome, populationState);

            case TRANSIENT -> genomeActivatorPool.create(genome, populationState);
        };
    }

    @Override
    public float evaluateFitness(final GenomeActivator genomeActivator) {
        return standardSecludedEnvironment.test(genomeActivator);
    }

    @Override
    public List<Float> evaluateAllFitness(final NeatContext context, final List<GenomeActivator> genomeActivators) {
        return standardCommunalEnvironment.test(context, genomeActivators);
    }

    @Override
    public void advanceGeneration(final int populationSize) {
        initialize(populationSize);
    }

    @Override
    public void clear() {
        historicalMarkings.clear();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("activation.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("activation.standardSecludedEnvironment", standardSecludedEnvironment);
        stateGroup.put("activation.standardCommunalEnvironment", standardCommunalEnvironment);
        stateGroup.put("activation.historicalMarkings", historicalMarkings);
    }

    private static StandardSecludedNeatEnvironment createStandardSecludedEnvironment(final StandardSecludedNeatEnvironment standardSecludedEnvironment, final NeatEnvironment environmentOverride) {
        if (standardSecludedEnvironment != null) {
            if (environmentOverride instanceof SecludedNeatEnvironment isolatedEnvironmentOverride) {
                standardSecludedEnvironment.override(isolatedEnvironmentOverride);

                return standardSecludedEnvironment;
            }

            Exception cause = standardSecludedEnvironment.getEnvironmentLoadException();

            if (cause != null) {
                throw new FitnessFunctionNotLoadedException("unable to load the isolated fitness function", cause);
            }
        }

        return standardSecludedEnvironment;
    }

    private static StandardCommunalNeatEnvironment createStandardCommunalEnvironment(final StandardCommunalNeatEnvironment standardCommunalEnvironment, final NeatEnvironment environmentOverride) {
        if (standardCommunalEnvironment == null) {
            return null;
        }

        if (environmentOverride instanceof CommunalNeatEnvironment sharedEnvironmentOverride) {
            standardCommunalEnvironment.override(sharedEnvironmentOverride);

            return standardCommunalEnvironment;
        }

        Exception cause = standardCommunalEnvironment.getEnvironmentLoadException();

        if (cause == null) {
            return standardCommunalEnvironment;
        }

        throw new FitnessFunctionNotLoadedException("unable to load the shared fitness function", cause);
    }

    static DefaultNeatContextActivationSupport create(final SerializableStateGroup stateGroup, final NeatEnvironment environmentOverride) {
        GenomeActivatorPool genomeActivatorPool = stateGroup.get("activation.genomeActivatorPool");
        StandardSecludedNeatEnvironment standardSecludedEnvironment = createStandardSecludedEnvironment(stateGroup.get("activation.standardSecludedEnvironment"), environmentOverride);
        StandardCommunalNeatEnvironment standardCommunalEnvironment = createStandardCommunalEnvironment(stateGroup.get("activation.standardCommunalEnvironment"), environmentOverride);
        HistoricalMarkings historicalMarkings = stateGroup.get("activation.historicalMarkings");

        return new DefaultNeatContextActivationSupport(genomeActivatorPool, standardSecludedEnvironment, standardCommunalEnvironment, historicalMarkings);
    }
}
