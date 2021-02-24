package com.experimental.ai.rl.neat;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class ConnectionGeneMap<T extends Comparable<T>> implements Iterable<ConnectionGene<T>> {
    private final SequentialMap<InnovationId<T>, ConnectionGene<T>> all = new SequentialMap<>();
    private final SequentialMap<InnovationId<T>, ConnectionGene<T>> expressed = new SequentialMap<>();
    private final Map<T, Map<DirectedEdge<T>, ConnectionGene<T>>> incomingToNodeId = new HashMap<>();
    private final Map<T, Map<DirectedEdge<T>, ConnectionGene<T>>> outgoingFromNodeId = new HashMap<>();

    public int sizeFromAll() {
        return all.size();
    }

    public int sizeFromExpressed() {
        return expressed.size();
    }

    public boolean isEmptyFromAll() {
        return all.isEmpty();
    }

    public boolean isEmptyFromExpressed() {
        return expressed.isEmpty();
    }

    public ConnectionGene<T> getByIdFromAll(final InnovationId<T> innovationId) {
        return all.getById(innovationId);
    }

    public ConnectionGene<T> getByIdFromExpressed(final InnovationId<T> innovationId) {
        return expressed.getById(innovationId);
    }

    public Map<DirectedEdge<T>, ConnectionGene<T>> getIncomingToNodeIdFromExpressed(final T nodeId) {
        return Optional.ofNullable(incomingToNodeId.get(nodeId))
                .orElseGet(ImmutableMap::of);
    }

    public Map<DirectedEdge<T>, ConnectionGene<T>> getIncomingToNodeFromExpressed(final NodeGene<T> node) {
        return getIncomingToNodeIdFromExpressed(node.getId());
    }

    public Map<DirectedEdge<T>, ConnectionGene<T>> getOutgoingFromNodeIdFromExpressed(final T nodeId) {
        return Optional.ofNullable(outgoingFromNodeId.get(nodeId))
                .orElseGet(ImmutableMap::of);
    }

    public Map<DirectedEdge<T>, ConnectionGene<T>> getOutgoingFromNodeFromExpressed(final NodeGene<T> node) {
        return getOutgoingFromNodeIdFromExpressed(node.getId());
    }

    public ConnectionGene<T> getLastFromAll() {
        return all.getLast();
    }

    public ConnectionGene<T> getLastFromExpressed() {
        return expressed.getLast();
    }

    private void addToExpressed(final ConnectionGene<T> connection) {
        expressed.put(connection.getInnovationId(), connection);

        incomingToNodeId.computeIfAbsent(connection.getInnovationId().getOutNodeId(), ni -> new LinkedHashMap<>())
                .put(connection.getInnovationId().getDirectedEdge(), connection);

        outgoingFromNodeId.computeIfAbsent(connection.getInnovationId().getInNodeId(), ni -> new LinkedHashMap<>())
                .put(connection.getInnovationId().getDirectedEdge(), connection);
    }

    public boolean put(final ConnectionGene<T> connection) {
        all.put(connection.getInnovationId(), connection);

        if (connection.isExpressed()) {
            addToExpressed(connection);

            return true;
        }

        return false;
    }

    private void removeFromExpressedIncomingAndOutgoing(final ConnectionGene<T> connection) {
        incomingToNodeId.computeIfPresent(connection.getInnovationId().getOutNodeId(), (k, oic) -> {
            oic.remove(connection.getInnovationId().getDirectedEdge());

            if (oic.isEmpty()) {
                return null;
            }

            return oic;
        });

        outgoingFromNodeId.computeIfPresent(connection.getInnovationId().getInNodeId(), (k, oic) -> {
            oic.remove(connection.getInnovationId().getDirectedEdge());

            if (oic.isEmpty()) {
                return null;
            }

            return oic;
        });
    }

    public ConnectionGene<T> disableByIndex(final int index) {
        ConnectionGene<T> connection = expressed.removeByIndex(index);

        if (connection == null) {
            return null;
        }

        removeFromExpressedIncomingAndOutgoing(connection);
        connection.disable();

        return connection;
    }

    void toggleExpressed(final ConnectionGene<T> connection) {
        if (connection == null) {
            return;
        }

        if (!connection.toggleExpressed()) {
            expressed.removeById(connection.getInnovationId());
            removeFromExpressedIncomingAndOutgoing(connection);
        } else {
            addToExpressed(connection);
        }
    }

    public ConnectionGene<T> toggleExpressedById(final InnovationId<T> innovationId) {
        ConnectionGene<T> connection = all.getById(innovationId);

        toggleExpressed(connection);

        return connection;
    }

    public Iterable<SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry> fullJoinFromExpressed(final ConnectionGeneMap<T> otherConnections) {
        return expressed.fullJoin(otherConnections.expressed);
    }

    public Iterable<SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry> fullJoinFromAll(final ConnectionGeneMap<T> otherConnections) {
        return all.fullJoin(otherConnections.all);
    }

    @Override
    public Iterator<ConnectionGene<T>> iterator() {
        return all.iterator();
    }
}
