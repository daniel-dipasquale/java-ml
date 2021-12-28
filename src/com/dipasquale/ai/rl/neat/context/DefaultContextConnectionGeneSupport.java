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
import com.dipasquale.ai.rl.neat.internal.DefaultRecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.internal.NoopRecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.internal.RecurrentModifiersFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeWeightPerturber;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.gate.DualModeIsLessThanRandomGate;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private FloatNumber.DualModeFactory weightFactory;
    private RecurrentModifiersFactory recurrentWeightsFactory;
    private DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber;
    private DualModeIsLessThanRandomGate shouldAllowRecurrentGate;
    private DualModeIsLessThanRandomGate shouldAllowMultiCycleGate;
    private GenesisGenomeConnector genesisGenomeConnector;
    private DualModeHistoricalMarkings historicalMarkings;

    private static RecurrentModifiersFactory createRecurrentWeightsFactory(final InitializationContext initializationContext, final ConnectionGeneSupport connectionGeneSupport, final FloatNumber.DualModeFactory weightFactory) {
        float recurrentAllowanceRate = connectionGeneSupport.getRecurrentAllowanceRate().getSingletonValue(initializationContext);

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return new NoopRecurrentModifiersFactory();
        }

        return new DefaultRecurrentModifiersFactory(weightFactory, connectionGeneSupport.getRecurrentStateType());
    }

    private static DualModeWeightPerturber<FloatNumber.DualModeFactory> createWeightPerturber(final InitializationContext initializationContext, final FloatNumber weightPerturber) {
        FloatNumber.DualModeFactory floatFactory = weightPerturber.createFactory(initializationContext);

        return new DualModeWeightPerturber<>(floatFactory);
    }

    private static DualModeIsLessThanRandomGate createIsLessThanRandomGate(final InitializationContext initializationContext, final FloatNumber max) {
        return new DualModeIsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), max.getSingletonValue(initializationContext));
    }

    public static DefaultContextConnectionGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSupport connectionGeneSupport) {
        FloatNumber.DualModeFactory weightFactory = connectionGeneSupport.getWeightFactory().createFactory(initializationContext);
        RecurrentModifiersFactory recurrentWeightsFactory = createRecurrentWeightsFactory(initializationContext, connectionGeneSupport, weightFactory);
        DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber = createWeightPerturber(initializationContext, connectionGeneSupport.getWeightPerturber());
        DualModeIsLessThanRandomGate shouldAllowRecurrentGate = createIsLessThanRandomGate(initializationContext, connectionGeneSupport.getRecurrentAllowanceRate());
        DualModeIsLessThanRandomGate shouldAllowMultiCycleGate = createIsLessThanRandomGate(initializationContext, connectionGeneSupport.getMultiCycleAllowanceRate());
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(initializationContext, weightFactory);
        DualModeHistoricalMarkings historicalMarkings = new DualModeHistoricalMarkings(initializationContext.getMapFactory());

        return new DefaultContextConnectionGeneSupport(weightFactory, recurrentWeightsFactory, weightPerturber, shouldAllowRecurrentGate, shouldAllowMultiCycleGate, genesisGenomeConnector, historicalMarkings);
    }

    @Override
    public float generateWeight() {
        return weightFactory.create();
    }

    @Override
    public List<Float> generateRecurrentWeights() {
        return recurrentWeightsFactory.create();
    }

    @Override
    public List<Float> cloneRecurrentWeights(final List<Float> recurrentWeights) {
        return recurrentWeightsFactory.clone(recurrentWeights);
    }

    @Override
    public List<Float> createAverageRecurrentWeights(final List<Float> recurrentWeights1, final List<Float> recurrentWeights2) {
        return recurrentWeightsFactory.createAverage(recurrentWeights1, recurrentWeights2);
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.perturb(weight);
    }

    @Override
    public boolean shouldAllowRecurrent() {
        return shouldAllowRecurrentGate.isOn();
    }

    @Override
    public boolean shouldAllowMultiCycle() {
        return shouldAllowMultiCycleGate.isOn();
    }

    @Override
    public void setupInitialConnections(final Genome genome) {
        genesisGenomeConnector.setupConnections(genome, this);
    }

    @Override
    public InnovationId provideInnovationId(final NodeGene sourceNode, final NodeGene targetNode) {
        return historicalMarkings.provideInnovationId(new DirectedEdge(sourceNode, targetNode));
    }

    @Override
    public boolean containsInnovationId(final InnovationId innovationId) {
        return historicalMarkings.containsInnovationId(innovationId.getDirectedEdge());
    }

    private static Iterable<? extends NodeGene> getHiddenNodes(final Genome genome) {
        return () -> genome.getNodes().iterator(NodeGeneType.HIDDEN);
    }

    @Override
    public void registerNodes(final Genome genome) {
        for (NodeGene node : getHiddenNodes(genome)) {
            historicalMarkings.registerNode(node);
        }
    }

    @Override
    public void deregisterNodes(final Genome genome) {
        for (NodeGene node : getHiddenNodes(genome)) {
            historicalMarkings.deregisterNode(node);
        }
    }

    @Override
    public void reset() {
        historicalMarkings.clear();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("connections.weightFactory", weightFactory);
        stateGroup.put("connections.recurrentWeightsFactory", recurrentWeightsFactory);
        stateGroup.put("connections.weightPerturber", weightPerturber);
        stateGroup.put("connections.shouldAllowRecurrentGate", shouldAllowRecurrentGate);
        stateGroup.put("connections.shouldAllowMultiCycleGate", shouldAllowMultiCycleGate);
        stateGroup.put("connections.genesisGenomeConnector", genesisGenomeConnector);
        stateGroup.put("connections.historicalMarkings", historicalMarkings);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        weightFactory = DualModeObject.activateMode(stateGroup.get("connections.weightFactory"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        recurrentWeightsFactory = stateGroup.get("connections.recurrentWeightsFactory");
        weightPerturber = DualModeObject.activateMode(stateGroup.get("connections.weightPerturber"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldAllowRecurrentGate = DualModeObject.activateMode(stateGroup.get("connections.shouldAllowRecurrentGate"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldAllowMultiCycleGate = DualModeObject.activateMode(stateGroup.get("connections.shouldAllowMultiCycleGate"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        genesisGenomeConnector = stateGroup.get("connections.genesisGenomeConnector");
        historicalMarkings = DualModeObject.activateMode(stateGroup.get("connections.historicalMarkings"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
