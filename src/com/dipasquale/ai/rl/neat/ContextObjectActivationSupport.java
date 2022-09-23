package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessBucket;
import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivatorPool;
import com.dipasquale.ai.rl.neat.phenotype.GruNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.LstmNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.data.structure.collection.IterableArray;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectActivationSupport implements Context.ActivationSupport {
    private final GenomeActivatorPool genomeActivatorPool;
    private final StandardSecludedNeatEnvironment standardIsolatedEnvironment;
    private final StandardCommunalNeatEnvironment standardSharedEnvironment;

    private ContextObjectActivationSupport(final GenomeActivatorPool genomeActivatorPool, final SecludedNeatEnvironment environment, final IterableArray<FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, new StandardSecludedNeatEnvironment(environment, fitnessBuckets), null);
    }

    private ContextObjectActivationSupport(final GenomeActivatorPool genomeActivatorPool, final CommunalNeatEnvironment environment, final IterableArray<FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, null, new StandardCommunalNeatEnvironment(environment, fitnessBuckets));
    }

    private static NeatNeuralNetworkFactory createNeuralNetworkFactory(final InitializationContext initializationContext, final ConnectionGeneSettings connectionGeneSettings, final ActivationSettings activationSettings) {
        float recurrentAllowanceRate = initializationContext.provideSingleton(connectionGeneSettings.getRecurrentAllowanceRate());

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeatNeuralNetworkFactory(activationSettings.getOutputTopologyDefinition());
        }

        return switch (connectionGeneSettings.getRecurrentStateType()) {
            case DEFAULT -> new RecurrentNeatNeuralNetworkFactory(activationSettings.getOutputTopologyDefinition());

            case LSTM -> new LstmNeatNeuralNetworkFactory(activationSettings.getOutputTopologyDefinition());

            case GRU -> new GruNeatNeuralNetworkFactory(activationSettings.getOutputTopologyDefinition());
        };
    }

    private static IterableArray<FitnessBucket> createFitnessBuckets(final InitializationContext initializationContext) {
        IterableArray<FitnessBucket> fitnessBuckets = initializationContext.createPopulationArray();
        FitnessControllerFactory fitnessControllerFactory = initializationContext.getFitnessControllerFactory();

        for (int i = 0, c = fitnessBuckets.capacity(); i < c; i++) {
            FitnessBucket fitnessBucket = new FitnessBucket(fitnessControllerFactory.create());

            fitnessBuckets.put(i, fitnessBucket);
        }

        return fitnessBuckets;
    }

    static ContextObjectActivationSupport create(final InitializationContext initializationContext, final ConnectionGeneSettings connectionGeneSettings, final ActivationSettings activationSettings) {
        NeatNeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(initializationContext, connectionGeneSettings, activationSettings);
        GenomeActivatorPool genomeActivatorPool = new GenomeActivatorPool(initializationContext.createPopulationArray(), neuralNetworkFactory);
        NeatEnvironment fitnessFunction = initializationContext.getFitnessFunction();
        IterableArray<FitnessBucket> fitnessBuckets = createFitnessBuckets(initializationContext);

        return switch (initializationContext.getEnvironmentType()) {
            case SECLUDED -> new ContextObjectActivationSupport(genomeActivatorPool, (SecludedNeatEnvironment) fitnessFunction, fitnessBuckets);

            case COMMUNAL -> new ContextObjectActivationSupport(genomeActivatorPool, (CommunalNeatEnvironment) fitnessFunction, fitnessBuckets);
        };
    }

    @Override
    public GenomeActivator provideActivator(final Genome genome, final PopulationState populationState, final Context.GenomeActivatorType type) {
        return switch (type) {
            case PERSISTENT -> genomeActivatorPool.provide(genome, populationState);

            case TRANSIENT -> genomeActivatorPool.create(genome, populationState);
        };
    }

    @Override
    public float calculateFitness(final GenomeActivator genomeActivator) {
        return standardIsolatedEnvironment.test(genomeActivator);
    }

    @Override
    public List<Float> calculateAllFitness(final Context context, final List<GenomeActivator> genomeActivators) {
        return standardSharedEnvironment.test(context, genomeActivators);
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("activation.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("activation.standardIsolatedEnvironment", standardIsolatedEnvironment);
        stateGroup.put("activation.standardSharedEnvironment", standardSharedEnvironment);
    }

    private static StandardSecludedNeatEnvironment createStandardIsolatedEnvironment(final StandardSecludedNeatEnvironment standardIsolatedEnvironment, final NeatEnvironment environmentOverride) {
        if (standardIsolatedEnvironment != null) {
            if (environmentOverride instanceof SecludedNeatEnvironment isolatedEnvironmentOverride) {
                standardIsolatedEnvironment.setEnvironment(isolatedEnvironmentOverride);
                standardIsolatedEnvironment.setEnvironmentLoadException(null);

                return standardIsolatedEnvironment;
            }

            Exception cause = standardIsolatedEnvironment.getEnvironmentLoadException();

            if (cause != null) {
                throw new FitnessFunctionNotLoadedException("unable to load the isolated fitness function", cause);
            }
        }

        return standardIsolatedEnvironment;
    }

    private static StandardCommunalNeatEnvironment createStandardSharedEnvironment(final StandardCommunalNeatEnvironment standardSharedEnvironment, final NeatEnvironment environmentOverride) {
        if (standardSharedEnvironment == null) {
            return null;
        }

        if (environmentOverride instanceof CommunalNeatEnvironment sharedEnvironmentOverride) {
            standardSharedEnvironment.setEnvironment(sharedEnvironmentOverride);
            standardSharedEnvironment.setEnvironmentLoadException(null);

            return standardSharedEnvironment;
        }

        Exception cause = standardSharedEnvironment.getEnvironmentLoadException();

        if (cause == null) {
            return standardSharedEnvironment;
        }

        throw new FitnessFunctionNotLoadedException("unable to load the shared fitness function", cause);
    }

    static ContextObjectActivationSupport create(final SerializableStateGroup stateGroup, final NeatEnvironment environmentOverride) {
        GenomeActivatorPool genomeActivatorPool = stateGroup.get("activation.genomeActivatorPool");
        StandardSecludedNeatEnvironment standardIsolatedEnvironment = createStandardIsolatedEnvironment(stateGroup.get("activation.standardIsolatedEnvironment"), environmentOverride);
        StandardCommunalNeatEnvironment standardSharedEnvironment = createStandardSharedEnvironment(stateGroup.get("activation.standardSharedEnvironment"), environmentOverride);

        return new ContextObjectActivationSupport(genomeActivatorPool, standardIsolatedEnvironment, standardSharedEnvironment);
    }
}
