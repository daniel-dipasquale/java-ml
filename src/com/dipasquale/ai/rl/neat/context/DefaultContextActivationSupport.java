package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.settings.ActivationSupport;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeGenomeActivatorPool;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

public final class DefaultContextActivationSupport implements Context.ActivationSupport {
    private DualModeGenomeActivatorPool genomeActivatorPool;
    private NeatEnvironment neatEnvironment;
    private FitnessDeterminerFactory fitnessDeterminerFactory;
    private DualModeMap<String, FitnessBucket> fitnessBuckets;
    private StandardNeatEnvironment standardNeatEnvironment;

    private DefaultContextActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final NeatEnvironment neatEnvironment, final FitnessDeterminerFactory fitnessDeterminerFactory, final DualModeMap<String, FitnessBucket> fitnessBuckets) {
        this.genomeActivatorPool = genomeActivatorPool;
        this.neatEnvironment = neatEnvironment;
        this.fitnessDeterminerFactory = fitnessDeterminerFactory;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }

    public static DefaultContextActivationSupport create(final ParallelismSupport parallelismSupport, final GeneralEvaluatorSupport generalEvaluatorSupport, final ActivationSupport activationSupport) {
        NeuralNetworkFactory neuralNetworkFactory = switch (activationSupport.getNeuralNetworkType()) {
            case FEED_FORWARD -> new FeedForwardNeuralNetworkFactory();

            default -> new RecurrentNeuralNetworkFactory();
        };

        DualModeGenomeActivatorPool genomeActivatorPool = new DualModeGenomeActivatorPool(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads(), neuralNetworkFactory);
        NeatEnvironment neatEnvironment = generalEvaluatorSupport.getFitnessFunction();
        FitnessDeterminerFactory fitnessDeterminerFactory = generalEvaluatorSupport.getFitnessDeterminerFactory();
        DualModeMap<String, FitnessBucket> fitnessBuckets = new DualModeMap<>(parallelismSupport.isEnabled(), parallelismSupport.getNumberOfThreads());

        return new DefaultContextActivationSupport(genomeActivatorPool, neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }

    @Override
    public GenomeActivator getOrCreateGenomeActivator(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.getOrCreate(genome, populationState);
    }

    @Override
    public float calculateFitness(final GenomeActivator genomeActivator) {
        return standardNeatEnvironment.test(genomeActivator);
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("neuralNetwork.genomeActivatorPool", genomeActivatorPool);
        stateGroup.put("neuralNetwork.neatEnvironment", neatEnvironment);
        stateGroup.put("neuralNetwork.fitnessDeterminerFactory", fitnessDeterminerFactory);
        stateGroup.put("neuralNetwork.fitnessBuckets", fitnessBuckets);
    }

    private static DualModeGenomeActivatorPool loadGenomeActivatorPool(final DualModeGenomeActivatorPool genomeActivatorPool, final IterableEventLoop eventLoop) {
        DualModeGenomeActivatorPool genomeActivatorPoolFixed = DualModeObject.switchMode(genomeActivatorPool, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeGenomeActivatorPool(false, 1, genomeActivatorPoolFixed);
        }

        return new DualModeGenomeActivatorPool(true, eventLoop.getConcurrencyLevel(), genomeActivatorPoolFixed);
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
        genomeActivatorPool = loadGenomeActivatorPool(stateGroup.get("neuralNetwork.genomeActivatorPool"), eventLoop);
        neatEnvironment = loadNeatEnvironment(stateGroup.get("neuralNetwork.neatEnvironment"), neatEnvironmentOverride);
        fitnessDeterminerFactory = stateGroup.get("neuralNetwork.fitnessDeterminerFactory");
        fitnessBuckets = DefaultContext.loadMap(stateGroup.get("neuralNetwork.fitnessBuckets"), eventLoop);
        standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }
}
