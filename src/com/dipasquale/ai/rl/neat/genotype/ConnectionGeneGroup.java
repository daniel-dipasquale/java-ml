package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.OrderedGroup;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.common.Pair;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ConnectionGeneGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 6378529672148503435L;
    @EqualsAndHashCode.Include
    private final All all = new All();
    private final Expressed expressed = new Expressed();

    public boolean put(final ConnectionGene connection) {
        all.connections.put(connection.getInnovationId(), connection);

        if (connection.isExpressed()) {
            expressed.add(connection);

            return true;
        }

        return false;
    }

    private static <TKey, TValue> Map<TKey, TValue> ensureNotNull(final Map<TKey, TValue> map) {
        if (map != null) {
            return map;
        }

        return ImmutableMap.of();
    }

    private static Map<DirectedEdge, ConnectionGene> removeIfEmpty(final Map<DirectedEdge, ConnectionGene> connections, final DirectedEdge directedEdge) {
        connections.remove(directedEdge);

        if (connections.isEmpty()) {
            return null;
        }

        return connections;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static final class All implements Iterable<ConnectionGene>, Serializable {
        @Serial
        private static final long serialVersionUID = -2991129194086625584L;
        private final OrderedGroup<InnovationId, ConnectionGene> connections = new OrderedGroup<>();

        public ConnectionGene getById(final InnovationId innovationId) {
            return connections.getById(innovationId);
        }

        public ConnectionGene getLast() {
            return connections.getLast();
        }

        @Override
        public Iterator<ConnectionGene> iterator() {
            return connections.iterator();
        }

        public Iterator<Pair<ConnectionGene>> fullJoin(final ConnectionGeneGroup other) {
            return connections.fullJoin(other.all.connections);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Expressed implements Serializable {
        @Serial
        private static final long serialVersionUID = -9128761857546984189L;
        private final OrderedGroup<InnovationId, ConnectionGene> connections = new OrderedGroup<>();
        private final Map<SequentialId, Map<DirectedEdge, ConnectionGene>> incomingToNodeId = new HashMap<>();
        private final Map<SequentialId, Map<DirectedEdge, ConnectionGene>> outgoingFromNodeId = new HashMap<>();

        public int size() {
            return connections.size();
        }

        public boolean isEmpty() {
            return connections.isEmpty();
        }

        public Map<DirectedEdge, ConnectionGene> getIncomingToNodeId(final SequentialId nodeId) {
            return ensureNotNull(incomingToNodeId.get(nodeId));
        }

        public Map<DirectedEdge, ConnectionGene> getIncomingToNode(final NodeGene node) {
            return getIncomingToNodeId(node.getId());
        }

        public Map<DirectedEdge, ConnectionGene> getOutgoingFromNodeId(final SequentialId nodeId) {
            return ensureNotNull(outgoingFromNodeId.get(nodeId));
        }

        public Map<DirectedEdge, ConnectionGene> getOutgoingFromNode(final NodeGene node) {
            return getOutgoingFromNodeId(node.getId());
        }

        private void add(final ConnectionGene connection) {
            SequentialId targetNodeId = connection.getInnovationId().getTargetNodeId();
            SequentialId sourceNodeId = connection.getInnovationId().getSourceNodeId();
            DirectedEdge directedEdge = connection.getInnovationId().getDirectedEdge();

            connections.put(connection.getInnovationId(), connection);
            incomingToNodeId.computeIfAbsent(targetNodeId, ni -> new LinkedHashMap<>()).put(directedEdge, connection);
            outgoingFromNodeId.computeIfAbsent(sourceNodeId, ni -> new LinkedHashMap<>()).put(directedEdge, connection);
        }

        public ConnectionGene disableByIndex(final int index) {
            ConnectionGene connection = connections.removeByIndex(index);
            SequentialId targetNodeId = connection.getInnovationId().getTargetNodeId();
            SequentialId sourceNodeId = connection.getInnovationId().getSourceNodeId();
            DirectedEdge directedEdge = connection.getInnovationId().getDirectedEdge();

            incomingToNodeId.computeIfPresent(targetNodeId, (k, oic) -> removeIfEmpty(oic, directedEdge));
            outgoingFromNodeId.computeIfPresent(sourceNodeId, (k, oic) -> removeIfEmpty(oic, directedEdge));
            connection.disable();

            return connection;
        }
    }
}