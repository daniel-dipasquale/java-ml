package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
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

    public DefaultContextActivationSupport(final DualModeGenomeActivatorPool genomeActivatorPool, final NeatEnvironment neatEnvironment, final FitnessDeterminerFactory fitnessDeterminerFactory, final DualModeMap<String, FitnessBucket> fitnessBuckets) {
        this.genomeActivatorPool = genomeActivatorPool;
        this.neatEnvironment = neatEnvironment;
        this.fitnessDeterminerFactory = fitnessDeterminerFactory;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }

    @Override
    public GenomeActivator getOrCreateGenomeActivator(final Genome genome, final PopulationState populationState) {
        return genomeActivatorPool.getOrCreate(genome, populationState);
    }

    @Override
    public float calculateFitness(final GenomeActivator genomeActivator) {
        return standardNeatEnvironment.test(genomeActivator);
    }

    public void save(final SerializableStateGroup state) {
        state.put("neuralNetwork.genomeActivatorPool", genomeActivatorPool);
        state.put("neuralNetwork.neatEnvironment", neatEnvironment);
        state.put("neuralNetwork.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("neuralNetwork.fitnessBuckets", fitnessBuckets);
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

    private static DualModeMap<String, FitnessBucket> loadFitnessBuckets(final DualModeMap<String, FitnessBucket> fitnessBuckets, final IterableEventLoop eventLoop) {
        DualModeMap<String, FitnessBucket> fitnessBucketsFixed = DualModeObject.switchMode(fitnessBuckets, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeMap<>(false, 1, fitnessBucketsFixed);
        }

        return new DualModeMap<>(true, eventLoop.getConcurrencyLevel(), fitnessBucketsFixed);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop, final NeatEnvironment neatEnvironmentOverride) {
        genomeActivatorPool = loadGenomeActivatorPool(state.get("neuralNetwork.genomeActivatorPool"), eventLoop);
        neatEnvironment = loadNeatEnvironment(state.get("neuralNetwork.neatEnvironment"), neatEnvironmentOverride);
        fitnessDeterminerFactory = state.get("neuralNetwork.fitnessDeterminerFactory");
        fitnessBuckets = loadFitnessBuckets(state.get("neuralNetwork.fitnessBuckets"), eventLoop);
        standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }
}
