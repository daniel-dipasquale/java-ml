package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeHistoricalMarkings;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultContextConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private DefaultContextConnectionGeneParameters params;
    private ObjectProfile<FloatFactory> weightFactory;
    private ObjectProfile<WeightPerturber> weightPerturber;
    private GenesisGenomeConnector genesisGenomeConnector;
    private DualModeHistoricalMarkings historicalMarkings;

    @Override
    public Context.ConnectionGeneParameters params() {
        return params;
    }

    @Override
    public float generateWeight() {
        return weightFactory.getObject().create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.getObject().perturb(weight);
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
            historicalMarkings.registerNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.OUTPUT)) {
            historicalMarkings.registerNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.BIAS)) {
            historicalMarkings.registerNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.HIDDEN)) {
            historicalMarkings.registerNodeId(node.getId());
        }
    }

    @Override
    public void deregisterNodes(final Genome genome) {
        for (NodeGene node : getNodes(genome, NodeGeneType.INPUT)) {
            historicalMarkings.deregisterNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.OUTPUT)) {
            historicalMarkings.deregisterNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.BIAS)) {
            historicalMarkings.deregisterNodeId(node.getId());
        }

        for (NodeGene node : getNodes(genome, NodeGeneType.HIDDEN)) {
            historicalMarkings.deregisterNodeId(node.getId());
        }
    }

    @Override
    public void reset() {
        historicalMarkings.clear();
    }

    public void save(final SerializableStateGroup state) {
        state.put("connections.params", params);
        state.put("connections.weightFactory", weightFactory);
        state.put("connections.weightPerturber", weightPerturber);
        state.put("connections.genomeGenesisConnector", genesisGenomeConnector);
        state.put("connections.historicalMarkings", historicalMarkings);
    }

    private static DualModeHistoricalMarkings loadHistoricalMarkings(final DualModeHistoricalMarkings historicalMarkings, final IterableEventLoop eventLoop) {
        DualModeHistoricalMarkings historicalMarkingsFixed = DualModeObject.switchMode(historicalMarkings, eventLoop != null);

        if (eventLoop == null) {
            return new DualModeHistoricalMarkings(false, 1, historicalMarkingsFixed);
        }

        return new DualModeHistoricalMarkings(true, eventLoop.getConcurrencyLevel(), historicalMarkingsFixed);
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        params = state.get("connections.params");
        weightFactory = ObjectProfile.switchProfile(state.get("connections.weightFactory"), eventLoop != null);
        weightPerturber = ObjectProfile.switchProfile(state.get("connections.weightPerturber"), eventLoop != null);
        genesisGenomeConnector = state.get("connections.genomeGenesisConnector");
        historicalMarkings = loadHistoricalMarkings(state.get("connections.historicalMarkings"), eventLoop);
    }
}
