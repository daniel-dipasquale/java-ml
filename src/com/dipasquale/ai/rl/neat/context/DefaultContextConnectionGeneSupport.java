package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.settings.ActivationSupport;
import com.dipasquale.ai.rl.neat.settings.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkType;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeWeightPerturber;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private DefaultContextConnectionGeneParameters params;
    private FloatNumber.DualModeFactory weightFactory;
    private DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber;
    private GenesisGenomeConnector genesisGenomeConnector;
    private DualModeHistoricalMarkings historicalMarkings;

    private static DualModeWeightPerturber<FloatNumber.DualModeFactory> createWeightPerturberProfile(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final FloatNumber weightPerturber) {
        FloatNumber.DualModeFactory floatFactory = weightPerturber.createFactory(parallelismSupport, randomSupports);

        return new DualModeWeightPerturber<>(floatFactory);
    }

    public static DefaultContextConnectionGeneSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final GenesisGenomeTemplate genesisGenomeTemplate, final ActivationSupport activationSupport, final ConnectionGeneSupport connectionGeneSupport) {
        DefaultContextConnectionGeneParameters params = DefaultContextConnectionGeneParameters.builder()
                .multipleRecurrentCyclesAllowed(activationSupport.getNeuralNetworkType() == NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                .build();

        FloatNumber.DualModeFactory weightFactory = connectionGeneSupport.getWeightFactory().createFactory(parallelismSupport, randomSupports);
        DualModeWeightPerturber<FloatNumber.DualModeFactory> weightPerturber = createWeightPerturberProfile(parallelismSupport, randomSupports, connectionGeneSupport.getWeightPerturber());
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(parallelismSupport, randomSupports, weightFactory);
        DualModeHistoricalMarkings historicalMarkings = new DualModeHistoricalMarkings(parallelismSupport.getMapFactory());

        return new DefaultContextConnectionGeneSupport(params, weightFactory, weightPerturber, genesisGenomeConnector, historicalMarkings);
    }

    @Override
    public Context.ConnectionGeneParameters params() {
        return params;
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
        stateGroup.put("connections.params", params);
        stateGroup.put("connections.weightFactory", weightFactory);
        stateGroup.put("connections.weightPerturber", weightPerturber);
        stateGroup.put("connections.genesisGenomeConnector", genesisGenomeConnector);
        stateGroup.put("connections.historicalMarkings", historicalMarkings);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        params = stateGroup.get("connections.params");
        weightFactory = DualModeObject.activateMode(stateGroup.get("connections.weightFactory"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
        weightPerturber = DualModeObject.activateMode(stateGroup.get("connections.weightPerturber"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
        genesisGenomeConnector = stateGroup.get("connections.genesisGenomeConnector");
        historicalMarkings = DualModeObject.activateMode(stateGroup.get("connections.historicalMarkings"), eventLoop == null ? 0 : eventLoop.getConcurrencyLevel());
    }
}
