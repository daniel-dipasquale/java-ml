package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.core.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.core.FitnessFunctionNotLoadedException;
import com.dipasquale.ai.rl.neat.core.GeneralSupport;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.core.SharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.internal.FitnessBucket;
import com.dipasquale.ai.rl.neat.internal.StandardIsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.internal.StandardSharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GruNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.LstmNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextActivationSupport implements Context.ActivationSupport {
    private DualModeGenomeActivatorPool genomeActivatorPool;
    private StandardIsolatedNeatEnvironment standardIsolatedEnvironment;
    private StandardSharedNeatEnvironment standardSharedEnvironment;

    private DefaultContextActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final IsolatedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, new StandardIsolatedNeatEnvironment(environment, fitnessBuckets), null);
    }

    private DefaultContextActivationSupport(final int concurrencyLevel, final DualModeGenomeActivatorPool genomeActivatorPool, final SharedNeatEnvironment environment, final Map<String, FitnessBucket> fitnessBuckets) {
        this(genomeActivatorPool, null, new StandardSharedNeatEnvironment(concurrencyLevel, environment, fitnessBuckets));
    }

    private static NeuralNetworkFactory createNeuralNetworkFactory(final InitializationContext initializationContext, final ConnectionGeneSupport connectionGeneSupport) {
        float recurrentAllowanceRate = connectionGeneSupport.getRecurrentAllowanceRate().getSingletonValue(initializationContext);

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeuralNetworkFactory();
        }

        return switch (connectionGeneSupport.getRecurrentStateType()) {
            case DEFAULT -> new RecurrentNeuralNetworkFactory();

            case LSTM -> new LstmNeuralNetworkFactory();

            case GRU -> new GruNeuralNetworkFactory();
        };
    }

    private static Map<String, FitnessBucket> createFitnessBuckets(final InitializationContext initializationContext, final GeneralSupport generalSupport) {
        int populationSize = generalSupport.getPopulationSize().getSingletonValue(initializationContext);
        DualModeIdFactory genomeIdFactory = new DualModeIdFactory(initializationContext.getConcurrencyLevel(), IdType.GENOME);
        FitnessDeterminerFactory fitnessDeterminerFactory = generalSupport.getFitnessDeterminerFactory();
        Map<String, FitnessBucket> fitnessBuckets = new HashMap<>();

        for (int i = 0; i < populationSize; i++) {
            String genomeId = genomeIdFactory.create().toString();
            FitnessBucket fitnessBucket = new FitnessBucket(fitnessDeterminerFactory.create());

            fitnessBuckets.put(genomeId, fitnessBucket);
        }

        return fitnessBuckets;
    }

    public static DefaultContextActivationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport, final ConnectionGeneSupport connectionGeneSupport) {
        DualModeMapFactory mapFactory = initializationContext.getMapFactory();
        NeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(initializationContext, connectionGeneSupport);
        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(mapFactory, neuralNetworkFactory);
        NeatEnvironment fitnessFunction = generalSupport.getFitnessFunction();
        Map<String, FitnessBucket> fitnessBuckets = createFitnessBuckets(initializationContext, generalSupport);

        return switch (initializationContext.getEnvironmentType()) {
            case ISOLATED -> new DefaultContextActivationSupport(genomeActivatorPool, (IsolatedNeatEnvironment) fitnessFunction, fitnessBuckets);

            case SHARED -> new DefaultContextActivationSupport(initializationContext.getConcurrencyLevel(), genomeActivatorPool, (SharedNeatEnvironment) fitnessFunction, fitnessBuckets);
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

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop, final NeatEnvironment environmentOverride) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop), environmentOverride);
    }
}
