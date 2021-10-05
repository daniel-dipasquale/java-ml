package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.settings.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeSequentialIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

import java.util.HashMap;
import java.util.Map;

public final class DefaultContextActivationSupport implements Context.ActivationSupport {
    private DualModeGenomeActivatorPool genomeActivatorPool;
    private NeatEnvironment neatEnvironment;
    private Map<String, FitnessBucket> fitnessBuckets;
    private StandardNeatEnvironment standardNeatEnvironment;

    private DefaultContextActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final NeatEnvironment neatEnvironment, final Map<String, FitnessBucket> fitnessBuckets) {
        this.genomeActivatorPool = genomeActivatorPool;
        this.neatEnvironment = neatEnvironment;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessBuckets);
    }

    private static NeuralNetworkFactory createNeuralNetworkFactory(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final ConnectionGeneSupport connectionGeneSupport) {
        float recurrentAllowanceRate = connectionGeneSupport.getRecurrentAllowanceRate().getSingletonValue(parallelismSupport, randomSupports);

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new FeedForwardNeuralNetworkFactory();
        }

        return new RecurrentNeuralNetworkFactory();
    }

    private static Map<String, FitnessBucket> createFitnessBuckets(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final GeneralEvaluatorSupport generalEvaluatorSupport) {
        int populationSize = generalEvaluatorSupport.getPopulationSize().getSingletonValue(parallelismSupport, randomSupports);
        DualModeSequentialIdFactory genomeIdFactory = new DualModeSequentialIdFactory(parallelismSupport.getConcurrencyLevel(), "genome");
        FitnessDeterminerFactory fitnessDeterminerFactory = generalEvaluatorSupport.getFitnessDeterminerFactory();
        Map<String, FitnessBucket> fitnessBuckets = new HashMap<>();

        for (int i = 0; i < populationSize; i++) {
            String genomeId = genomeIdFactory.create().toString();

            fitnessBuckets.put(genomeId, new FitnessBucket(fitnessDeterminerFactory.create()));
        }

        return fitnessBuckets;
    }

    public static DefaultContextActivationSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final GeneralEvaluatorSupport generalEvaluatorSupport, final ConnectionGeneSupport connectionGeneSupport) {
        DualModeMapFactory mapFactory = parallelismSupport.getMapFactory();
        NeuralNetworkFactory neuralNetworkFactory = createNeuralNetworkFactory(parallelismSupport, randomSupports, connectionGeneSupport);
        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(mapFactory, neuralNetworkFactory);
        NeatEnvironment neatEnvironment = generalEvaluatorSupport.getFitnessFunction();
        Map<String, FitnessBucket> fitnessBuckets = createFitnessBuckets(parallelismSupport, randomSupports, generalEvaluatorSupport);

        return new DefaultContextActivationSupport(genomeActivatorPool, neatEnvironment, fitnessBuckets);
    }

    @Override
    public GenomeActivator getOrCreateActivator(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.getOrCreate(genome, populationState);
    }

    @Override
    public GenomeActivator createTransientActivator(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.createTransient(genome, populationState);
    }

    @Override
    public float calculateFitness(final GenomeActivator genomeActivator) {
        return standardNeatEnvironment.test(genomeActivator);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("activation.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("activation.neatEnvironment", neatEnvironment);
        stateGroup.put("activation.fitnessBuckets", fitnessBuckets);
    }

    private static NeatEnvironment loadNeatEnvironment(final Object neatEnvironment, final NeatEnvironment neatEnvironmentOverride) {
        if (neatEnvironmentOverride != null) {
            return neatEnvironmentOverride;
        }

        if (neatEnvironment instanceof NeatEnvironment) {
            return (NeatEnvironment) neatEnvironment;
        }

        if (neatEnvironment instanceof Throwable) {
            throw new RuntimeException("unable to load the neat environment (aka: fitness function)", (Throwable) neatEnvironment);
        }

        throw new RuntimeException("unable to load the neat environment (aka: fitness function)");
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop, final NeatEnvironment neatEnvironmentOverride) {
        genomeActivatorPool = DualModeObject.activateMode(stateGroup.get("activation.genomeActivatorPool"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        neatEnvironment = loadNeatEnvironment(stateGroup.get("activation.neatEnvironment"), neatEnvironmentOverride);
        fitnessBuckets = stateGroup.get("activation.fitnessBuckets");
        standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessBuckets);
    }
}
