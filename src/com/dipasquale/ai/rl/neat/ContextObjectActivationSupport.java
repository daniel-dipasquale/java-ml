package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessControllerFactory;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.internal.FitnessBucket;
import com.dipasquale.ai.rl.neat.internal.StandardIsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.internal.StandardSharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GruNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.LstmNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeatNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectActivationSupport implements Context.ActivationSupport {
    private DualModeGenomeActivatorPool genomeActivatorPool;
    private StandardIsolatedNeatEnvironment standardIsolatedEnvironment;
    private StandardSharedNeatEnvironment standardSharedEnvironment;

    private ContextObjectActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final IsolatedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, new StandardIsolatedNeatEnvironment(environment, fitnessBuckets), null);
    }

    private ContextObjectActivationSupport(final int concurrencyLevel, final DualModeGenomeActivatorPool genomeActivatorPool, final SharedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, null, new StandardSharedNeatEnvironment(concurrencyLevel, environment, fitnessBuckets));
    }

    private static NeatNeuralNetworkFactory createNeuralNetworkFactory(final InitializationContext initializationContext, final ConnectionGeneSupport connectionGeneSupport, final ActivationSupport activationSupport) {
        float recurrentAllowanceRate = initializationContext.getFloatSingleton(connectionGeneSupport.getRecurrentAllowanceRate());

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeatNeuralNetworkFactory(activationSupport.getOutputTopologyDefinition());
        }

        return switch (connectionGeneSupport.getRecurrentStateType()) {
            case DEFAULT -> new RecurrentNeatNeuralNetworkFactory(activationSupport.getOutputTopologyDefinition());

            case LSTM -> new LstmNeatNeuralNetworkFactory(activationSupport.getOutputTopologyDefinition());

            case GRU -> new GruNeatNeuralNetworkFactory(activationSupport.getOutputTopologyDefinition());
        };
    }

    private static Map<String, FitnessBucket> createFitnessBuckets(final InitializationContext initializationContext, final GeneralSupport generalSupport) {
        int populationSize = generalSupport.getPopulationSize();
        DualModeIdFactory genomeIdFactory = new DualModeIdFactory(initializationContext.getConcurrencyLevel(), IdType.GENOME);
        FitnessControllerFactory fitnessControllerFactory = generalSupport.getFitnessControllerFactory();
        Map<String, FitnessBucket> fitnessBuckets = new HashMap<>();

        for (int i = 0; i < populationSize; i++) {
            String genomeId = genomeIdFactory.create().toString();
            FitnessBucket fitnessBucket = new FitnessBucket(fitnessControllerFactory.create());

            fitnessBuckets.put(genomeId, fitnessBucket);
        }

        return fitnessBuckets;
    }

    static ContextObjectActivationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport, final ConnectionGeneSupport connectionGeneSupport, final ActivationSupport activationSupport) {
        NeatNeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(initializationContext, connectionGeneSupport, activationSupport);
        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(initializationContext.createMap(), neuralNetworkFactory);
        NeatEnvironment fitnessFunction = generalSupport.getFitnessFunction();
        Map<String, FitnessBucket> fitnessBuckets = createFitnessBuckets(initializationContext, generalSupport);

        return switch (initializationContext.getEnvironmentType()) {
            case ISOLATED -> new ContextObjectActivationSupport(genomeActivatorPool, (IsolatedNeatEnvironment) fitnessFunction, fitnessBuckets);

            case SHARED -> new ContextObjectActivationSupport(initializationContext.getConcurrencyLevel(), genomeActivatorPool, (SharedNeatEnvironment) fitnessFunction, fitnessBuckets);
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

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("activation.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("activation.standardIsolatedEnvironment", standardIsolatedEnvironment);
        stateGroup.put("activation.standardSharedEnvironment", standardSharedEnvironment);
    }

    private static StandardIsolatedNeatEnvironment loadStandardIsolatedEnvironment(final StandardIsolatedNeatEnvironment standardIsolatedEnvironment, final NeatEnvironment environmentOverride) {
        if (standardIsolatedEnvironment != null) {
            if (environmentOverride instanceof IsolatedNeatEnvironment isolatedEnvironmentOverride) {
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

    private static StandardSharedNeatEnvironment loadStandardSharedEnvironment(final StandardSharedNeatEnvironment standardSharedEnvironment, final NeatEnvironment environmentOverride, final int concurrencyLevel) {
        if (standardSharedEnvironment != null) {
            if (environmentOverride instanceof SharedNeatEnvironment sharedEnvironmentOverride) {
                standardSharedEnvironment.setEnvironment(sharedEnvironmentOverride);
                standardSharedEnvironment.setEnvironmentLoadException(null);

                return DualModeObject.activateMode(standardSharedEnvironment, concurrencyLevel);
            }

            Exception cause = standardSharedEnvironment.getEnvironmentLoadException();

            if (cause != null) {
                throw new FitnessFunctionNotLoadedException("unable to load the shared fitness function", cause);
            }

            return DualModeObject.activateMode(standardSharedEnvironment, concurrencyLevel);
        }

        return null;
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel, final NeatEnvironment environmentOverride) {
        genomeActivatorPool = DualModeObject.activateMode(stateGroup.get("activation.genomeActivatorPool"), concurrencyLevel);
        standardIsolatedEnvironment = loadStandardIsolatedEnvironment(stateGroup.get("activation.standardIsolatedEnvironment"), environmentOverride);
        standardSharedEnvironment = loadStandardSharedEnvironment(stateGroup.get("activation.standardSharedEnvironment"), environmentOverride, concurrencyLevel);
    }

    public void load(final SerializableStateGroup stateGroup, final ParallelEventLoop eventLoop, final NeatEnvironment environmentOverride) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop), environmentOverride);
    }
}
