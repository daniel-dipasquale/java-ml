package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class HistoricalMarkings {
    private final SequentialIdFactory innovationIdFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final ObjectFactory<NodeGeneIdDependencyTracker> nodeIdDependencyTrackerFactory;
    private final Map<SequentialId, NodeGeneIdDependencyTracker> nodeIdDependencyTrackers;

    private void add(final SequentialId nodeId, final DirectedEdge directedEdge) {
        nodeIdDependencyTrackers.computeIfAbsent(nodeId, nid -> nodeIdDependencyTrackerFactory.create()).add(directedEdge);
    }

    private InnovationId createInnovationId(final DirectedEdge directedEdge) {
        add(directedEdge.getSourceNodeId(), directedEdge);
        add(directedEdge.getTargetNodeId(), directedEdge);

        return new InnovationId(directedEdge, innovationIdFactory.create());
    }

    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, this::createInnovationId);
    }

    public boolean containsInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.containsKey(directedEdge);
    }

    public void registerNode(final NodeGene node) {
        nodeIdDependencyTrackers.computeIfAbsent(node.getId(), nid -> nodeIdDependencyTrackerFactory.create()).increaseBlastRadius();
    }

    public void deregisterNode(final NodeGene node) {
        NodeGeneIdDependencyTracker nodeIdDependencyTracker = nodeIdDependencyTrackers.get(node.getId());

        if (nodeIdDependencyTracker.decreaseBlastRadius() == 0) {
            nodeIdDependencyTracker.removeFrom(innovationIds);
        }
    }

    public void clear() {
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeIdDependencyTrackers.clear();
    }
}
