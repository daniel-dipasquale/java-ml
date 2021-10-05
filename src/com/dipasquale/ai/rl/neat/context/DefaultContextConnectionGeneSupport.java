package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.settings.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeWeightPerturber;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private FloatNumber.DualModeFactory weightFactory;
    private DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber;
    private DualModeIsLessThanRandomGateProvider shouldAllowRecurrentGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldAllowMultiCycleGateProvider;
    private GenesisGenomeConnector genesisGenomeConnector;
    private DualModeHistoricalMarkings historicalMarkings;

    private static DualModeWeightPerturber<FloatNumber.DualModeFactory> createWeightPerturber(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final FloatNumber weightPerturber) {
        FloatNumber.DualModeFactory floatFactory = weightPerturber.createFactory(parallelismSupport, randomSupports);

        return new DualModeWeightPerturber<>(floatFactory);
    }

    private static DualModeIsLessThanRandomGateProvider createIsLessThanRandomGateProvider(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final FloatNumber max) {
        return new DualModeIsLessThanRandomGateProvider(randomSupport, max.getSingletonValue(parallelismSupport, randomSupports));
    }

    public static DefaultContextConnectionGeneSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSupport connectionGeneSupport) {
        FloatNumber.DualModeFactory weightFactory = connectionGeneSupport.getWeightFactory().createFactory(parallelismSupport, randomSupports);
        DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber = createWeightPerturber(parallelismSupport, randomSupports, connectionGeneSupport.getWeightPerturber());
        DualModeIsLessThanRandomGateProvider shouldAllowRecurrentGateProvider = createIsLessThanRandomGateProvider(parallelismSupport, randomSupports, randomSupport, connectionGeneSupport.getRecurrentAllowanceRate());
        DualModeIsLessThanRandomGateProvider shouldAllowMultiCycleGateProvider = createIsLessThanRandomGateProvider(parallelismSupport, randomSupports, randomSupport, connectionGeneSupport.getMultiCycleAllowanceRate());
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(parallelismSupport, randomSupports, weightFactory);
        DualModeHistoricalMarkings historicalMarkings = new DualModeHistoricalMarkings(parallelismSupport.getMapFactory());

        return new DefaultContextConnectionGeneSupport(weightFactory, weightPerturber, shouldAllowRecurrentGateProvider, shouldAllowMultiCycleGateProvider, genesisGenomeConnector, historicalMarkings);
    }

    @Override
    public float generateWeight() {
        return weightFactory.create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.perturb(weight);
    }

    @Override
    public boolean shouldAllowRecurrent() {
        return shouldAllowRecurrentGateProvider.isOn();
    }

    @Override
    public boolean shouldAllowMultiCycle() {
        return shouldAllowMultiCycleGateProvider.isOn();
    }

    @Override
    public void setupInitialConnections(final Genome genome) {
        genesisGenomeConnector.setupConnections(genome, this);
    }

    @Override
    public InnovationId getOrCreateInnovationId(final NodeGene inputNode, final NodeGene outputNode) {
        return historicalMarkings.getOrCreateInnovationId(new DirectedEdge(inputNode, outputNode));
    }

    @Override
    public boolean containsInnovationId(final InnovationId innovationId) {
        return historicalMarkings.containsInnovationId(innovationId.getDirectedEdge());
    }

    private static Iterable<? extends NodeGene> getNodes(final Genome genome, final NodeGeneType type) {
        return () -> genome.getNodes().iterator(type);
    }

    @Override
    public void registerNodes(final Genome genome) {
        for (NodeGene node : getNodes(genome, NodeGeneType.INPUT)) {
            historicalMarkings.registerNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.OUTPUT)) {
            historicalMarkings.registerNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.BIAS)) {
            historicalMarkings.registerNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.HIDDEN)) {
            historicalMarkings.registerNode(node);
        }
    }

    @Override
    public void deregisterNodes(final Genome genome) {
        for (NodeGene node : getNodes(genome, NodeGeneType.INPUT)) {
            historicalMarkings.deregisterNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.OUTPUT)) {
            historicalMarkings.deregisterNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.BIAS)) {
            historicalMarkings.deregisterNode(node);
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.HIDDEN)) {
            historicalMarkings.deregisterNode(node);
        }
    }

    @Override
    public void reset() {
        historicalMarkings.clear();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("connections.weightFactory", weightFactory);
        stateGroup.put("connections.weightPerturber", weightPerturber);
        stateGroup.put("connections.shouldAllowRecurrentGateProvider", shouldAllowRecurrentGateProvider);
        stateGroup.put("connections.shouldAllowMultiCycleGateProvider", shouldAllowMultiCycleGateProvider);
        stateGroup.put("connections.genesisGenomeConnector", genesisGenomeConnector);
        stateGroup.put("connections.historicalMarkings", historicalMarkings);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        weightFactory = DualModeObject.activateMode(stateGroup.get("connections.weightFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        weightPerturber = DualModeObject.activateMode(stateGroup.get("connections.weightPerturber"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldAllowRecurrentGateProvider = DualModeObject.activateMode(stateGroup.get("connections.shouldAllowRecurrentGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldAllowMultiCycleGateProvider = DualModeObject.activateMode(stateGroup.get("connections.shouldAllowMultiCycleGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        genesisGenomeConnector = stateGroup.get("connections.genesisGenomeConnector");
        historicalMarkings = DualModeObject.activateMode(stateGroup.get("connections.historicalMarkings"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
