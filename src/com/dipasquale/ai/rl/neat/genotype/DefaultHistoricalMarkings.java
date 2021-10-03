package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultHistoricalMarkings<T extends NodeGeneIdDependencyTracker> implements HistoricalMarkings {
    private final SequentialIdFactory innovationIdFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final ObjectFactory<T> nodeIdDependencyTrackerFactory;
    private final Map<SequentialId, T> nodeIdDependencyTrackers;

    private void addDependency(final SequentialId nodeId, final DirectedEdge directedEdge) {
        NodeGeneIdDependencyTracker nodeIdDependencyTracker = nodeIdDependencyTrackers.computeIfAbsent(nodeId, nid -> nodeIdDependencyTrackerFactory.create());

        nodeIdDependencyTracker.add(directedEdge);
    }

    private InnovationId createInnovationId(final DirectedEdge directedEdge) {
        addDependency(directedEdge.getSourceNodeId(), directedEdge);
        addDependency(directedEdge.getTargetNodeId(), directedEdge);

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
        NodeGeneIdDependencyTracker nodeIdDependencyTracker = nodeIdDependencyTrackers.computeIfAbsent(node.getId(), nid -> nodeIdDependencyTrackerFactory.create());

        nodeIdDependencyTracker.increaseBlastRadius();
    }

    @Override
    public void deregisterNode(final NodeGene node) {
        NodeGeneIdDependencyTracker nodeIdDependencyTracker = nodeIdDependencyTrackers.get(node.getId());

        if (nodeIdDependencyTracker.decreaseBlastRadius() == 0) {
            nodeIdDependencyTracker.removeFrom(innovationIds);
        }
    }

    @Override
    public void clear() {
        innovationIdFactory.reset();
        innovationIds.clear();
        nodeIdDependencyTrackers.clear();
    }
}
