package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.core.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeWeightPerturber;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private FloatNumber.DualModeFactory weightFactory;
    private DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber;
    private DualModeIsLessThanRandomGateProvider shouldAllowRecurrentGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldAllowMultiCycleGateProvider;
    private GenesisGenomeConnector genesisGenomeConnector;
    private DualModeHistoricalMarkings historicalMarkings;

    private static DualModeWeightPerturber<FloatNumber.DualModeFactory> createWeightPerturber(final InitializationContext initializationContext, final FloatNumber weightPerturber) {
        FloatNumber.DualModeFactory floatFactory = weightPerturber.createFactory(initializationContext);

        return new DualModeWeightPerturber<>(floatFactory);
    }

    private static DualModeIsLessThanRandomGateProvider createIsLessThanRandomGateProvider(final InitializationContext initializationContext, final FloatNumber max) {
        return new DualModeIsLessThanRandomGateProvider(initializationContext.createDefaultRandomSupport(), max.getSingletonValue(initializationContext));
    }

    public static DefaultContextConnectionGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSupport connectionGeneSupport) {
        FloatNumber.DualModeFactory weightFactory = connectionGeneSupport.getWeightFactory().createFactory(initializationContext);
        DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber = createWeightPerturber(initializationContext, connectionGeneSupport.getWeightPerturber());
        DualModeIsLessThanRandomGateProvider shouldAllowRecurrentGateProvider = createIsLessThanRandomGateProvider(initializationContext, connectionGeneSupport.getRecurrentAllowanceRate());
        DualModeIsLessThanRandomGateProvider shouldAllowMultiCycleGateProvider = createIsLessThanRandomGateProvider(initializationContext, connectionGeneSupport.getMultiCycleAllowanceRate());
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(initializationContext, weightFactory);
        DualModeHistoricalMarkings historicalMarkings = new DualModeHistoricalMarkings(initializationContext.getMapFactory());

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
    public InnovationId provideInnovationId(final NodeGene inputNode, final NodeGene outputNode) {
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
        for (NodeGene node : getNodes(genome, NodeGeneType.HIDDEN)) {
            historicalMarkings.registerNode(node);
        }
    }

    @Override
    public void deregisterNodes(final Genome genome) {
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
