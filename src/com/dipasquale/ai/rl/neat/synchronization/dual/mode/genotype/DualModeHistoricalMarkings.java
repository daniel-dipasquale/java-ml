package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.DefaultHistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeMapToSetFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

public final class DualModeHistoricalMarkings implements HistoricalMarkings, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2505708313091427289L;
    private final DualModeIdFactory innovationIdFactory;
    private final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds;
    private final NodeGeneDependencyTrackerFactory nodeDependencyTrackerFactory;
    private final DualModeMap<Id, DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeDependencyTrackers;
    private transient DefaultHistoricalMarkings<DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>> historicalMarkings;

    private DualModeHistoricalMarkings(final DualModeMapFactory mapFactory, final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds, final NodeGeneDependencyTrackerFactory nodeDependencyTrackerFactory, final DualModeMap<Id, DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeDependencyTrackers) {
        this.innovationIdFactory = new DualModeIdFactory(mapFactory.concurrencyLevel(), "innovation-id");
        this.innovationIds = innovationIds;
        this.nodeDependencyTrackerFactory = nodeDependencyTrackerFactory;
        this.nodeDependencyTrackers = nodeDependencyTrackers;
        this.historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }

    public DualModeHistoricalMarkings(final DualModeMapFactory mapFactory) {
        this(mapFactory, new DualModeMap<>(mapFactory), new NodeGeneDependencyTrackerFactory(new DualModeMapToSetFactory(mapFactory)), new DualModeMap<>(mapFactory));
    }

    @Override
    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return historicalMarkings.getOrCreateInnovationId(directedEdge);
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
    public int concurrencyLevel() {
        return innovationIdFactory.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        innovationIdFactory.activateMode(concurrencyLevel);
        innovationIds.activateMode(concurrencyLevel);
        nodeDependencyTrackerFactory.activateMode(concurrencyLevel);
        DualModeObject.forEachValueActivateMode(nodeDependencyTrackers, concurrencyLevel);
        nodeDependencyTrackers.activateMode(concurrencyLevel);
        historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeDependencyTrackerFactory, nodeDependencyTrackers);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NodeGeneDependencyTrackerFactory implements ObjectFactory<DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>>, DualModeObject, Serializable {
        @Serial
        private static final long serialVersionUID = 107984128175600587L;
        private final DualModeMapToSetFactory setFactory;

        @Override
        public DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory> create() {
            return new DualModeNodeGeneDependencyTracker<>(setFactory);
        }

        @Override
        public int concurrencyLevel() {
            return setFactory.concurrencyLevel();
        }

        @Override
        public void activateMode(final int concurrencyLevel) {
            setFactory.activateMode(concurrencyLevel);
        }
    }
}
