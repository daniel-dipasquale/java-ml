package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.common.Id;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype.DualModeIdFactory;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultHistoricalMarkings<T extends NodeGeneDependencyTracker> implements HistoricalMarkings {
    private final DualModeIdFactory innovationIdFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final ObjectFactory<T> nodeDependencyTrackerFactory;
    private final Map<Id, T> nodeDependencyTrackers;

    private NodeGeneDependencyTracker getOrCreateNodeDependencyTracker(final Id nodeId) {
        return nodeDependencyTrackers.computeIfAbsent(nodeId, nid -> nodeDependencyTrackerFactory.create());
    }

    private void addDependencyToNode(final Id nodeId, final DirectedEdge directedEdge) {
        getOrCreateNodeDependencyTracker(nodeId).addEdge(directedEdge);
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
        getOrCreateNodeDependencyTracker(node.getId()).increaseBlastRadius();
    }

    @Override
    public void deregisterNode(final NodeGene node) {
        NodeGeneDependencyTracker nodeDependencyTracker = nodeDependencyTrackers.get(node.getId());
        int blastRadius = nodeDependencyTracker.decreaseBlastRadius();

        if (blastRadius == 0) {
            nodeDependencyTracker.removeEdgesFrom(innovationIds);
        }

        assert blastRadius >= 0;
    }

    @Override
    public void clear() {
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeDependencyTrackers.clear();
    }
}
