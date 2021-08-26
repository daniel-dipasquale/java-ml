package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.common.FitnessBucket;
import com.dipasquale.ai.rl.neat.common.StandardNeatEnvironment;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneGroup;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneGroup;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.phenotype.DualModeNeuralNetworkHub;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;

public final class DefaultContextNeuralNetworkSupport implements Context.NeuralNetworkSupport {
    private DualModeNeuralNetworkHub hub;
    private NeatEnvironment neatEnvironment;
    private FitnessDeterminerFactory fitnessDeterminerFactory;
    private DualModeMap<String, FitnessBucket> fitnessBuckets;
    private StandardNeatEnvironment standardNeatEnvironment;

    public DefaultContextNeuralNetworkSupport(final DualModeNeuralNetworkHub hub, final NeatEnvironment neatEnvironment, final FitnessDeterminerFactory fitnessDeterminerFactory, final DualModeMap<String, FitnessBucket> fitnessBuckets) {
        this.hub = hub;
        this.neatEnvironment = neatEnvironment;
        this.fitnessDeterminerFactory = fitnessDeterminerFactory;
        this.fitnessBuckets = fitnessBuckets;
        this.standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }

    @Override
    public float calculateFitness(final DefaultGenome genome, final NodeGeneGroup nodes, final ConnectionGeneGroup connections, final PopulationState populationState) {
        Genome proxyGenome = hub.getOrCreateProxyGenome(genome, nodes, connections, populationState);

        return standardNeatEnvironment.test(proxyGenome);
    }

    @Override
    public NeuralNetwork getPhenotype(final DefaultGenome genome) {
        return hub.getPhenotype(genome);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("neuralNetwork.hub", hub);
        state.put("neuralNetwork.neatEnvironment", neatEnvironment);
        state.put("neuralNetwork.fitnessDeterminerFactory", fitnessDeterminerFactory);
        state.put("neuralNetwork.fitnessBuckets", fitnessBuckets);
    }

    private static DualModeNeuralNetworkHub loadHub(final DualModeNeuralNetworkHub hub, final IterableEventLoop eventLoop) {
        DualModeNeuralNetworkHub hubFixed = DualModeObject.switchMode(hub, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeNeuralNetworkHub(false, 1, hubFixed);
        }

        return new DualModeNeuralNetworkHub(true, eventLoop.getConcurrencyLevel(), hubFixed);
    }

    private static NeatEnvironment loadNeatEnvironment(final Object neatEnvironment, final NeatEnvironment neatEnvironmentOverride) {
        if (neatEnvironmentOverride != null) {
            return neatEnvironmentOverride;
        }

        if (neatEnvironment instanceof Throwable) {
            throw new RuntimeException("unable to load the neat environment (aka: fitness function)", (Throwable) neatEnvironment);
        }

        return (NeatEnvironment) neatEnvironment;
    }

    private static DualModeMap<String, FitnessBucket> loadFitnessBuckets(final DualModeMap<String, FitnessBucket> fitnessBuckets, final IterableEventLoop eventLoop) {
        DualModeMap<String, FitnessBucket> fitnessBucketsFixed = DualModeObject.switchMode(fitnessBuckets, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeMap<>(false, 1, fitnessBucketsFixed);
        }

        return new DualModeMap<>(true, eventLoop.getConcurrencyLevel(), fitnessBucketsFixed);
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop, final NeatEnvironment neatEnvironmentOverride) {
        hub = loadHub(state.get("neuralNetwork.hub"), eventLoop);
        neatEnvironment = loadNeatEnvironment(state.get("neuralNetwork.neatEnvironment"), neatEnvironmentOverride);
        fitnessDeterminerFactory = state.get("neuralNetwork.fitnessDeterminerFactory");
        fitnessBuckets = loadFitnessBuckets(state.get("neuralNetwork.fitnessBuckets"), eventLoop);
        standardNeatEnvironment = new StandardNeatEnvironment(neatEnvironment, fitnessDeterminerFactory, fitnessBuckets);
    }
}
