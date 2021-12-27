package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultHistoricalMarkings<T extends NodeGeneDependencyTracker> implements HistoricalMarkings {
    private final DualModeIdFactory innovationIdFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final ObjectFactory<T> nodeDependencyTrackerFactory;
    private final Map<Id, T> nodeDependencyTrackers;

    private void addDependencyToNode(final Id nodeId, final DirectedEdge directedEdge) {
        NodeGeneDependencyTracker nodeDependencyTracker = nodeDependencyTrackers.get(nodeId);

        if (nodeDependencyTracker != null) {
            nodeDependencyTracker.addEdge(directedEdge);
        }
    }

    private InnovationId createInnovationId(final DirectedEdge directedEdge) {
        addDependencyToNode(directedEdge.getSourceNodeId(), directedEdge);
        addDependencyToNode(directedEdge.getTargetNodeId(), directedEdge);

        return new InnovationId(directedEdge, innovationIdFactory.create());
    }

    @Override
    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, this::createInnovationId);
    }

    @Override
    public boolean containsInnovationId(final DirectedEdge directedEdge) {
        return innovationIds.containsKey(directedEdge);
    }

    @Override
    public void registerNode(final NodeGene node) {
        NodeGeneDependencyTracker nodeDependencyTracker = nodeDependencyTrackers.computeIfAbsent(node.getId(), nid -> nodeDependencyTrackerFactory.create());

        nodeDependencyTracker.increaseBlastRadius();
    }

    @Override
    public void deregisterNode(final NodeGene node) {
        NodeGeneDependencyTracker nodeDependencyTracker = nodeDependencyTrackers.get(node.getId());
        int blastRadius = nodeDependencyTracker.decreaseBlastRadius();

        if (blastRadius == 0) {
            nodeDependencyTracker.removeEdgesFrom(innovationIds);
            nodeDependencyTrackers.remove(node.getId());
        }
    }

    @Override
    public void clear() {
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeDependencyTrackers.clear();
    }
}
