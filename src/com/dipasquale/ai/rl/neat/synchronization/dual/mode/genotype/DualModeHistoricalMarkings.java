package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneIdDependencyTracker;
import com.dipasquale.ai.rl.neat.synchronization.dual.mode.DualModeSequentialIdFactory;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.common.serialization.SerializableSupport;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public final class DualModeHistoricalMarkings implements DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 2505708313091427289L;
    private final DualModeSequentialIdFactory innovationIdFactory;
    private final DualModeMap<DirectedEdge, InnovationId> innovationIds;
    private final NodeIdDependencyTrackerFactory nodeIdDependencyTrackerFactory;
    private final DualModeMap<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackers;
    private transient HistoricalMarkings historicalMarkings;

    private DualModeHistoricalMarkings(final DualModeSequentialIdFactory innovationIdFactory, final DualModeMap<DirectedEdge, InnovationId> innovationIds, final NodeIdDependencyTrackerFactory nodeIdDependencyTrackerFactory, final DualModeMap<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackers) {
        this.innovationIdFactory = innovationIdFactory;
        this.innovationIds = innovationIds;
        this.nodeIdDependencyTrackerFactory = nodeIdDependencyTrackerFactory;
        this.nodeIdDependencyTrackers = nodeIdDependencyTrackers;
        this.historicalMarkings = createHistoricalMarkings(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    private DualModeHistoricalMarkings(final boolean concurrent, final int numberOfThreads, final Map<DirectedEdge, InnovationId> innovationIds, final Map<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackers) {
        this(new DualModeSequentialIdFactory(concurrent, "innovation-id"), new DualModeMap<>(concurrent, numberOfThreads, innovationIds), new NodeIdDependencyTrackerFactory(concurrent, numberOfThreads), createNodeIdDependencyTrackers(concurrent, numberOfThreads, nodeIdDependencyTrackers));
    }

    public DualModeHistoricalMarkings(final boolean concurrent, final int numberOfThreads, final DualModeHistoricalMarkings historicalMarkings) {
        this(concurrent, numberOfThreads, historicalMarkings.innovationIds, historicalMarkings.nodeIdDependencyTrackers);
    }

    public DualModeHistoricalMarkings(final boolean concurrent, final int numberOfThreads) {
        this(concurrent, numberOfThreads, null, null);
    }

    private static DualModeMap<SequentialId, DualModeNodeIdDependencyTracker> createNodeIdDependencyTrackers(final boolean concurrent, final int numberOfThreads, final Map<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackers) {
        if (nodeIdDependencyTrackers == null) {
            return new DualModeMap<>(concurrent, numberOfThreads, null);
        }

        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.put("nodeIdDependencyTrackers", nodeIdDependencyTrackers);

        try {
            byte[] stateGroupBytes = SerializableSupport.serializeStateGroup(stateGroup);
            SerializableStateGroup clonedStateGroup = SerializableSupport.deserializeStateGroup(stateGroupBytes);
            Map<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackersFixed = clonedStateGroup.get("nodeIdDependencyTrackers");

            nodeIdDependencyTrackersFixed.values().forEach(niddt -> niddt.switchMode(concurrent));

            return new DualModeMap<>(concurrent, numberOfThreads, nodeIdDependencyTrackersFixed);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to clone the nodeIdDependencyTrackers", e);
        }
    }

    private static HistoricalMarkings createHistoricalMarkings(final DualModeSequentialIdFactory innovationIdFactory, final DualModeMap<DirectedEdge, InnovationId> innovationIds, final NodeIdDependencyTrackerFactory nodeIdDependencyTrackerFactory, final DualModeMap<SequentialId, DualModeNodeIdDependencyTracker> nodeIdDependencyTrackers) {
        return new HistoricalMarkings(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, (Map<SequentialId, NodeGeneIdDependencyTracker>) (Object) nodeIdDependencyTrackers);
    }

    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        return historicalMarkings.getOrCreateInnovationId(directedEdge);
    }

    public boolean containsInnovationId(final DirectedEdge directedEdge) {
        return historicalMarkings.containsInnovationId(directedEdge);
    }

    public void registerNode(final NodeGene node) {
        historicalMarkings.registerNode(node);
    }

    public void deregisterNode(final NodeGene node) {
        historicalMarkings.deregisterNode(node);
    }

    public void clear() {
        historicalMarkings.clear();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        innovationIdFactory.switchMode(concurrent);
        innovationIds.switchMode(concurrent);
        nodeIdDependencyTrackerFactory.switchMode(concurrent);
        nodeIdDependencyTrackers.switchMode(concurrent);
        nodeIdDependencyTrackers.values().forEach(niddt -> niddt.switchMode(concurrent));
        historicalMarkings = createHistoricalMarkings(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        historicalMarkings = createHistoricalMarkings(innovationIdFactory, innovationIds, nodeIdDependencyTrackerFactory, nodeIdDependencyTrackers);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NodeIdDependencyTrackerFactory implements ObjectFactory<NodeGeneIdDependencyTracker>, DualModeObject, Serializable {
        @Serial
        private static final long serialVersionUID = 107984128175600587L;
        private boolean parallel;
        private final int numberOfThreads;

        @Override
        public NodeGeneIdDependencyTracker create() {
            return new DualModeNodeIdDependencyTracker(parallel, numberOfThreads);
        }

        @Override
        public void switchMode(final boolean concurrent) {
            parallel = concurrent;
        }
    }
}
