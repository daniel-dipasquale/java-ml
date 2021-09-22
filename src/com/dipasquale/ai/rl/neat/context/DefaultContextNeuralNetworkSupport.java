package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeNeuralNetworkHub;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

public final class DefaultContextNeuralNetworkSupport implements Context.NeuralNetworkSupport {
    private DualModeNeuralNetworkHub neuralNetworkHub;
    private NeatEnvironment neatEnvironment;
    private FitnessDeterminerFactory fitnessDeterminerFactory;
    private DualModeMap<String, FitnessBucket> fitnessBuckets;
    private StandardNeatEnvironment standardNeatEnvironment;

    public DefaultContextNeuralNetworkSupport(final DualModeNeuralNetworkHub neuralNetworkHub, final NeatEnvironment neatEnvironment, final FitnessDeterminerFactory fitnessDeterminerFactory, final DualModeMap<String, FitnessBucket> fitnessBuckets) {
        this.neuralNetworkHub = neuralNetworkHub;
        this.neatEnvironment = neatEnvironment;
        this.fitnessDeterminerFactory = fitnessDeterminerFactory;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }

    @Override
    public GenomeActivator getOrCreateGenomeActivator(final Genome genome, final PopulationState populationState) {
        return neuralNetworkHub.getOrCreateGenomeActivator(genome, populationState);
    }

    @Override
    public float calculateFitness(final GenomeActivator genomeActivator) {
        return standardNeatEnvironment.test(genomeActivator);
    }

    public void save(final SerializableStateGroup state) {
        state.put("neuralNetwork.neuralNetworkHub", neuralNetworkHub);
        state.put("neuralNetwork.neatEnvironment", neatEnvironment);
        state.put("neuralNetwork.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("neuralNetwork.fitnessBuckets", fitnessBuckets);
    }

    private static DualModeNeuralNetworkHub loadNeuralNetworkHub(final DualModeNeuralNetworkHub neuralNetworkHub, final IterableEventLoop eventLoop) {
        DualModeNeuralNetworkHub neuralNetworkHubFixed = DualModeObject.switchMode(neuralNetworkHub, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeNeuralNetworkHub(false, 1, neuralNetworkHubFixed);
        }

        return new DualModeNeuralNetworkHub(true, eventLoop.getConcurrencyLevel(), neuralNetworkHubFixed);
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
        neuralNetworkHub = loadNeuralNetworkHub(state.get("neuralNetwork.neuralNetworkHub"), eventLoop);
        neatEnvironment = loadNeatEnvironment(state.get("neuralNetwork.neatEnvironment"), neatEnvironmentOverride);
        fitnessDeterminerFactory = state.get("neuralNetwork.fitnessDeterminerFactory");
        fitnessBuckets = loadFitnessBuckets(state.get("neuralNetwork.fitnessBuckets"), eventLoop);
        standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }
}
