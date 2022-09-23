package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.IdFactory;
import com.dipasquale.ai.rl.neat.IdType;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class HistoricalMarkings implements Serializable {
    @Serial
    private static final long serialVersionUID = -2411435935049736384L;
    private final SequentialIdFactory<Id> innovationIdFactory = new IdFactory(IdType.INNOVATION_ID);
    private final Map<DirectedEdge, InnovationId> innovationIds = new HashMap<>();
    private final Map<Id, NodeGeneDependencyTracker> nodeGeneDependencyTrackers = new HashMap<>();

    private void addDependencyToNodeGene(final Id nodeGeneId, final DirectedEdge directedEdge) {
        NodeGeneDependencyTracker nodeDependencyTracker = nodeGeneDependencyTrackers.get(nodeGeneId);

        if (nodeDependencyTracker != null) {
            nodeDependencyTracker.addEdge(directedEdge);
        }
    }

    private InnovationId createInnovationId(final DirectedEdge directedEdge) {
        addDependencyToNodeGene(directedEdge.getSourceNodeGeneId(), directedEdge);
        addDependencyToNodeGene(directedEdge.getTargetNodeGeneId(), directedEdge);

        return new InnovationId(directedEdge, innovationIdFactory.create());
    }

    public InnovationId provideInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, this::createInnovationId);
    }

    public boolean containsInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.containsKey(directedEdge);
    }

    public void registerNodeGene(final NodeGene nodeGene) {
        NodeGeneDependencyTracker nodeGeneDependencyTracker = nodeGeneDependencyTrackers.computeIfAbsent(nodeGene.getId(), __ -> new NodeGeneDependencyTracker());

        nodeGeneDependencyTracker.increaseBlastRadius();
    }

    public void deregisterNodeGene(final NodeGene nodeGene) {
        NodeGeneDependencyTracker nodeGeneDependencyTracker = nodeGeneDependencyTrackers.get(nodeGene.getId());
        int blastRadius = nodeGeneDependencyTracker.decreaseBlastRadius();

        if (blastRadius == 0) {
            nodeGeneDependencyTracker.removeEdgesFrom(innovationIds);
            nodeGeneDependencyTrackers.remove(nodeGene.getId());
        } else {
            assert blastRadius > 0;
        }
    }

    public void clear() {
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeGeneDependencyTrackers.clear();
    }
}
