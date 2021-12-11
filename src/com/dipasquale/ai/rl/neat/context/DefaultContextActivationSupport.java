package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.common.StandardSharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.core.FitnessFunctionNotLoadedException;
import com.dipasquale.ai.rl.neat.core.GeneralSupport;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironmentType;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.core.SharedNeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.DefaultRecurrentNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.GruNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.LstmNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultContextActivationSupport implements Context.ActivationSupport {
    private DualModeGenomeActivatorPool genomeActivatorPool;
    private NeatEnvironmentType neatEnvironmentType;
    private IsolatedNeatEnvironment isolatedNeatEnvironment;
    private SharedNeatEnvironment sharedNeatEnvironment;
    private Map<String, FitnessBucket> fitnessBuckets;
    private StandardNeatEnvironment standardNeatEnvironment;
    private StandardSharedNeatEnvironment standardSharedNeatEnvironment;

    private DefaultContextActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final NeatEnvironmentType neatEnvironmentType, final IsolatedNeatEnvironment isolatedNeatEnvironment, final SharedNeatEnvironment sharedNeatEnvironment, final Map<String, FitnessBucket> fitnessBuckets) {
        this.genomeActivatorPool = genomeActivatorPool;
        this.neatEnvironmentType = neatEnvironmentType;
        this.isolatedNeatEnvironment = isolatedNeatEnvironment;
        this.sharedNeatEnvironment = sharedNeatEnvironment;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(isolatedNeatEnvironment, fitnessBuckets);
        this.standardSharedNeatEnvironment = new StandardSharedNeatEnvironment(sharedNeatEnvironment, fitnessBuckets);
    }

    private static NeuralNetworkFactory createNeuralNetworkFactory(final InitializationContext initializationContext, final ConnectionGeneSupport connectionGeneSupport) {
        float recurrentAllowanceRate = connectionGeneSupport.getRecurrentAllowanceRate().getSingletonValue(initializationContext);

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeuralNetworkFactory();
        }

        return switch (connectionGeneSupport.getRecurrentStateType()) {
            case DEFAULT -> new DefaultRecurrentNeuralNetworkFactory();

            case LSTM -> new LstmNeuralNetworkFactory();

            case GRU -> new GruNeuralNetworkFactory();
        };
    }

    private static Map<String, FitnessBucket> createFitnessBuckets(final InitializationContext initializationContext, final GeneralSupport generalSupport) {
        int populationSize = generalSupport.getPopulationSize().getSingletonValue(initializationContext);
        DualModeIdFactory genomeIdFactory = new DualModeIdFactory(initializationContext.getParallelism().getConcurrencyLevel(), "genome");
        FitnessDeterminerFactory fitnessDeterminerFactory = generalSupport.getFitnessDeterminerFactory();
        Map<String, FitnessBucket> fitnessBuckets = new HashMap<>();

        for (int i = 0; i < populationSize; i++) {
            String genomeId = genomeIdFactory.create().toString();

            fitnessBuckets.put(genomeId, new FitnessBucket(fitnessDeterminerFactory.create()));
        }

        return Collections.unmodifiableMap(fitnessBuckets);
    }

    public static DefaultContextActivationSupport create(final InitializationContext initializationContext, final GeneralSupport generalSupport, final ConnectionGeneSupport connectionGeneSupport) {
        DualModeMapFactory mapFactory = initializationContext.getParallelism().getMapFactory();
        NeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(initializationContext, connectionGeneSupport);
        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(mapFactory, neuralNetworkFactory);
        NeatEnvironment fitnessFunction = generalSupport.getFitnessFunction();
        Map<String, FitnessBucket> fitnessBuckets = createFitnessBuckets(initializationContext, generalSupport);

        return switch (initializationContext.getNeatEnvironmentType()) {
            case ISOLATED -> new DefaultContextActivationSupport(genomeActivatorPool, NeatEnvironmentType.ISOLATED, (IsolatedNeatEnvironment) fitnessFunction, null, fitnessBuckets);

            case SHARED -> new DefaultContextActivationSupport(genomeActivatorPool, NeatEnvironmentType.SHARED, null, (SharedNeatEnvironment) fitnessFunction, fitnessBuckets);
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
        return standardNeatEnvironment.test(genomeActivator);
    }

    @Override
    public List<Float> calculateAllFitness(final List<GenomeActivator> genomeActivators) {
        return standardSharedNeatEnvironment.test(genomeActivators);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("activation.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("activation.neatEnvironmentType", neatEnvironmentType);
        stateGroup.put("activation.isolatedNeatEnvironment", isolatedNeatEnvironment);
        stateGroup.put("activation.sharedNeatEnvironment", sharedNeatEnvironment);
        stateGroup.put("activation.fitnessBuckets", fitnessBuckets);
    }

    private static IsolatedNeatEnvironment loadNeatEnvironment(final Object neatEnvironment, final NeatEnvironment fitnessFunctionOverride, final NeatEnvironmentType neatEnvironmentType) {
        if (fitnessFunctionOverride instanceof IsolatedNeatEnvironment isolatedNeatEnvironmentOverride) {
            if (neatEnvironmentType == NeatEnvironmentType.ISOLATED) {
                return isolatedNeatEnvironmentOverride;
            }

            throw new FitnessFunctionNotLoadedException("unable to override environment with a fitness function that isn't isolated");
        }

        if (neatEnvironment instanceof IsolatedNeatEnvironment isolatedNeatEnvironmentFixed) {
            return isolatedNeatEnvironmentFixed;
        }

        if (neatEnvironment instanceof Throwable exception) {
            throw new FitnessFunctionNotLoadedException("unable to load the isolated fitness function", exception);
        }

        return null;
    }

    private static SharedNeatEnvironment loadSharedNeatEnvironment(final Object neatEnvironment, final NeatEnvironment fitnessFunctionOverride, final NeatEnvironmentType neatEnvironmentType) {
        if (fitnessFunctionOverride instanceof SharedNeatEnvironment sharedNeatEnvironmentOverride) {
            if (neatEnvironmentType == NeatEnvironmentType.SHARED) {
                return sharedNeatEnvironmentOverride;
            }

            throw new FitnessFunctionNotLoadedException("unable to override environment with a fitness function that isn't shared");
        }

        if (neatEnvironment instanceof SharedNeatEnvironment sharedNeatEnvironmentOverride) {
            return sharedNeatEnvironmentOverride;
        }

        if (neatEnvironment instanceof Throwable exception) {
            throw new FitnessFunctionNotLoadedException("unable to load the shared fitness function", exception);
        }

        return null;
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop, final NeatEnvironment fitnessFunctionOverride) {
        genomeActivatorPool = DualModeObject.activateMode(stateGroup.get("activation.genomeActivatorPool"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        neatEnvironmentType = stateGroup.get("activation.neatEnvironmentType");
        isolatedNeatEnvironment = loadNeatEnvironment(stateGroup.get("activation.isolatedNeatEnvironment"), fitnessFunctionOverride, neatEnvironmentType);
        sharedNeatEnvironment = loadSharedNeatEnvironment(stateGroup.get("activation.sharedNeatEnvironment"), fitnessFunctionOverride, neatEnvironmentType);
        fitnessBuckets = stateGroup.get("activation.fitnessBuckets");
        standardNeatEnvironment = new StandardNeatEnvironment(isolatedNeatEnvironment, fitnessBuckets);
        standardSharedNeatEnvironment = new StandardSharedNeatEnvironment(sharedNeatEnvironment, fitnessBuckets);
    }
}
