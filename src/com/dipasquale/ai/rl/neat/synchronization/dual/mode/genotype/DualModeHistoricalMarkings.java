package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.StateStrategyHistoricalMarkings;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.DualModeIdFactory;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal.IdType;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeMapToSetFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

public final class DualModeHistoricalMarkings implements HistoricalMarkings, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2505708313091427289L;
    private final DualModeIdFactory innovationIdFactory;
    private final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds;
    private final DualModeNodeGeneDependencyTrackerFactory nodeDependencyTrackerFactory;
    private final DualModeMap<Id, DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeDependencyTrackers;
    private transient StateStrategyHistoricalMarkings<DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>> historicalMarkings;

    private DualModeHistoricalMarkings(final int concurrencyLevel, final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds, final DualModeNodeGeneDependencyTrackerFactory nodeDependencyTrackerFactory, final DualModeMap<Id, DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeDependencyTrackers) {
        this.innovationIdFactory = new DualModeIdFactory(concurrencyLevel, IdType.INNOVATION_ID);
        this.innovationIds = innovationIds;
        this.nodeDependencyTrackerFactory = nodeDependencyTrackerFactory;
        this.nodeDependencyTrackers = nodeDependencyTrackers;
        this.historicalMarkings = new StateStrategyHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }

    public DualModeHistoricalMarkings(final int concurrencyLevel, final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds, final DualModeMapToSetFactory setFactory, final DualModeMap<Id, DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeDependencyTrackers) {
        this(concurrencyLevel, innovationIds, new DualModeNodeGeneDependencyTrackerFactory(concurrencyLevel, setFactory), nodeDependencyTrackers);
    }

    @Override
    public InnovationId provideInnovationId(final DirectedEdge directedEdge) {
        return historicalMarkings.provideInnovationId(directedEdge);
    }

    @Override
    public boolean containsInnovationId(final DirectedEdge directedEdge) {
        return historicalMarkings.containsInnovationId(directedEdge);
    }

    @Override
    public void registerNode(final NodeGene node) {
        historicalMarkings.registerNode(node);
    }

    @Override
    public void deregisterNode(final NodeGene node) {
        historicalMarkings.deregisterNode(node);
    }

    @Override
    public void clear() {
        historicalMarkings.clear();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        innovationIdFactory.activateMode(concurrencyLevel);
        innovationIds.activateMode(concurrencyLevel);
        nodeDependencyTrackerFactory.activateMode(concurrencyLevel);
        DualModeObject.forEachValueActivateMode(nodeDependencyTrackers, concurrencyLevel);
        nodeDependencyTrackers.activateMode(concurrencyLevel);
        historicalMarkings = new StateStrategyHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        historicalMarkings = new StateStrategyHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }
}
