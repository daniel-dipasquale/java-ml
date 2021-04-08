package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialMap;
import com.dipasquale.common.Pair;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ConnectionGeneMap implements Iterable<ConnectionGene>, Serializable {
    @Serial
    private static final long serialVersionUID = 6378529672148503435L;
    private final SequentialMap<InnovationId, ConnectionGene> all = new SequentialMap<>();
    private final SequentialMap<InnovationId, ConnectionGene> expressed = new SequentialMap<>();
    private final Map<SequentialId, Map<DirectedEdge, ConnectionGene>> incomingToNodeId = new HashMap<>();
    private final Map<SequentialId, Map<DirectedEdge, ConnectionGene>> outgoingFromNodeId = new HashMap<>();

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

    public ConnectionGene getByIdFromAll(final InnovationId innovationId) {
        return all.getById(innovationId);
    }

    public ConnectionGene getByIdFromExpressed(final InnovationId innovationId) {
        return expressed.getById(innovationId);
    }

    public ConnectionGene getByIndexFromExpressed(final int index) {
        return expressed.getByIndex(index);
    }

    public Map<DirectedEdge, ConnectionGene> getIncomingToNodeIdFromExpressed(final SequentialId nodeId) {
        return Optional.ofNullable(incomingToNodeId.get(nodeId))
                .orElseGet(ImmutableMap::of);
    }

    public Map<DirectedEdge, ConnectionGene> getIncomingToNodeFromExpressed(final NodeGene node) {
        return getIncomingToNodeIdFromExpressed(node.getId());
    }

    public Map<DirectedEdge, ConnectionGene> getOutgoingFromNodeIdFromExpressed(final SequentialId nodeId) {
        return Optional.ofNullable(outgoingFromNodeId.get(nodeId))
                .orElseGet(ImmutableMap::of);
    }

    public Map<DirectedEdge, ConnectionGene> getOutgoingFromNodeFromExpressed(final NodeGene node) {
        return getOutgoingFromNodeIdFromExpressed(node.getId());
    }

    public ConnectionGene getLastFromAll() {
        return all.getLast();
    }

    public ConnectionGene getLastFromExpressed() {
        return expressed.getLast();
    }

    private void addToExpressed(final ConnectionGene connection) {
        expressed.put(connection.getInnovationId(), connection);

        incomingToNodeId.computeIfAbsent(connection.getInnovationId().getTargetNodeId(), ni -> new LinkedHashMap<>())
                .put(connection.getInnovationId().getDirectedEdge(), connection);

        outgoingFromNodeId.computeIfAbsent(connection.getInnovationId().getSourceNodeId(), ni -> new LinkedHashMap<>())
                .put(connection.getInnovationId().getDirectedEdge(), connection);
    }

    public boolean put(final ConnectionGene connection) {
        all.put(connection.getInnovationId(), connection);

        if (connection.isExpressed()) {
            addToExpressed(connection);

            return true;
        }

        return false;
    }

    private void removeFromExpressedIncomingAndOutgoing(final ConnectionGene connection) {
        incomingToNodeId.computeIfPresent(connection.getInnovationId().getTargetNodeId(), (k, oic) -> {
            oic.remove(connection.getInnovationId().getDirectedEdge());

            if (oic.isEmpty()) {
                return null;
            }

            return oic;
        });

        outgoingFromNodeId.computeIfPresent(connection.getInnovationId().getSourceNodeId(), (k, oic) -> {
            oic.remove(connection.getInnovationId().getDirectedEdge());

            if (oic.isEmpty()) {
                return null;
            }

            return oic;
        });
    }

    public ConnectionGene disableByIndex(final int index) {
        ConnectionGene connection = expressed.removeByIndex(index);

        if (connection == null) {
            return null;
        }

        removeFromExpressedIncomingAndOutgoing(connection);
        connection.disable();

        return connection;
    }

    void toggleExpressed(final ConnectionGene connection) {
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

    public ConnectionGene toggleExpressedById(final InnovationId innovationId) {
        ConnectionGene connection = all.getById(innovationId);

        toggleExpressed(connection);

        return connection;
    }

    public Iterable<Pair<ConnectionGene>> fullJoinFromExpressed(final ConnectionGeneMap other) {
        return expressed.fullJoin(other.expressed);
    }

    public Iterable<Pair<ConnectionGene>> fullJoinFromAll(final ConnectionGeneMap other) {
        return all.fullJoin(other.all);
    }

    @Override
    public Iterator<ConnectionGene> iterator() {
        return all.iterator();
    }
}
