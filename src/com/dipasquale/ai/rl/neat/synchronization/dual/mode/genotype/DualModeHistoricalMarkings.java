package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.DefaultHistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
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
    private final DualModeSequentialIdFactory innovationIdFactory;
    private final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds;
    private final NodeGeneIdDependencyTrackerFactory nodeIdDependencyTrackerFactory;
    private final DualModeMap<SequentialId, DualModeNodeGeneIdDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeIdDependencyTrackers;
    private transient DefaultHistoricalMarkings<DualModeNodeGeneIdDependencyTracker<DualModeMapToSetFactory>> historicalMarkings;

    private DualModeHistoricalMarkings(final DualModeMapFactory mapFactory, final DualModeMap<DirectedEdge, InnovationId, DualModeMapFactory> innovationIds, final DualModeMap<SequentialId, DualModeNodeGeneIdDependencyTracker<DualModeMapToSetFactory>, DualModeMapFactory> nodeIdDependencyTrackers) {
        this.innovationIdFactory = new DualModeSequentialIdFactory(mapFactory.concurrencyLevel(), "innovation-id");
        this.innovationIds = innovationIds;
        this.nodeIdDependencyTrackerFactory = new NodeGeneIdDependencyTrackerFactory(new DualModeMapToSetFactory(mapFactory));
        this.nodeIdDependencyTrackers = nodeIdDependencyTrackers;
        this.historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    public DualModeHistoricalMarkings(final DualModeMapFactory mapFactory) {
        this(mapFactory, new DualModeMap<>(mapFactory), new DualModeMap<>(mapFactory));
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
        nodeIdDependencyTrackerFactory.activateMode(concurrencyLevel);
        DualModeObject.forEachValueActivateMode(nodeIdDependencyTrackers, concurrencyLevel);
        nodeIdDependencyTrackers.activateMode(concurrencyLevel);
        historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        historicalMarkings = new DefaultHistoricalMarkings<>(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NodeGeneIdDependencyTrackerFactory implements ObjectFactory<DualModeNodeGeneIdDependencyTracker<DualModeMapToSetFactory>>, DualModeObject, Serializable {
        @Serial
        private static final long serialVersionUID = 107984128175600587L;
        private final DualModeMapToSetFactory setFactory;

        @Override
        public DualModeNodeGeneIdDependencyTracker<DualModeMapToSetFactory> create() {
            return new DualModeNodeGeneIdDependencyTracker<>(setFactory);
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
