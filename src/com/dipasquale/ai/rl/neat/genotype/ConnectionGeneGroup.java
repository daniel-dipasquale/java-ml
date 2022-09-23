package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.common.Pair;
import com.dipasquale.data.structure.group.ElementKeyAccessor;
import com.dipasquale.data.structure.group.ListSetGroup;
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

    boolean put(final ConnectionGene connectionGene) {
        all.connectionGenes.put(connectionGene);

        if (connectionGene.isExpressed()) {
            expressed.add(connectionGene);

            return true;
        }

        return false;
    }

    private static ListSetGroup<InnovationId, ConnectionGene> createConnectionGenes() {
        return new ListSetGroup<>((ElementKeyAccessor<InnovationId, ConnectionGene> & Serializable) ConnectionGene::getInnovationId);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static final class All implements Iterable<ConnectionGene>, Serializable {
        @Serial
        private static final long serialVersionUID = -2991129194086625584L;
        private final ListSetGroup<InnovationId, ConnectionGene> connectionGenes = createConnectionGenes();

        public int size() {
            return connectionGenes.size();
        }

        public ConnectionGene getById(final InnovationId innovationId) {
            return connectionGenes.getById(innovationId);
        }

        public ConnectionGene getLast() {
            return connectionGenes.getLast();
        }

        @Override
        public Iterator<ConnectionGene> iterator() {
            return connectionGenes.iterator();
        }

        public Iterator<Pair<ConnectionGene>> fullJoin(final ConnectionGeneGroup other) {
            return connectionGenes.fullJoin(other.all.connectionGenes);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Expressed implements Serializable {
        @Serial
        private static final long serialVersionUID = -9128761857546984189L;
        private final ListSetGroup<InnovationId, ConnectionGene> connectionGenes = createConnectionGenes();
        private final Map<Id, Map<DirectedEdge, ConnectionGene>> incomingToNodeGeneId = new HashMap<>();
        private final Map<Id, Map<DirectedEdge, ConnectionGene>> outgoingFromNodeGeneId = new HashMap<>();

        public int size() {
            return connectionGenes.size();
        }

        public boolean isEmpty() {
            return connectionGenes.isEmpty();
        }

        public ConnectionGene getByIndex(final int index) {
            return connectionGenes.getByIndex(index);
        }

        private static <TKey, TValue> Map<TKey, TValue> ensureNotNull(final Map<TKey, TValue> map) {
            if (map != null) {
                return map;
            }

            return Map.of();
        }

        public Map<DirectedEdge, ConnectionGene> getIncomingToNodeGeneId(final Id nodeGeneId) {
            return ensureNotNull(incomingToNodeGeneId.get(nodeGeneId));
        }

        public Map<DirectedEdge, ConnectionGene> getIncomingToNodeGene(final NodeGene nodeGene) {
            return getIncomingToNodeGeneId(nodeGene.getId());
        }

        public Map<DirectedEdge, ConnectionGene> getOutgoingFromNodeGeneId(final Id nodeGeneId) {
            return ensureNotNull(outgoingFromNodeGeneId.get(nodeGeneId));
        }

        public Map<DirectedEdge, ConnectionGene> getOutgoingFromNodeGene(final NodeGene nodeGene) {
            return getOutgoingFromNodeGeneId(nodeGene.getId());
        }

        private static Map<DirectedEdge, ConnectionGene> removeIfEmpty(final Map<DirectedEdge, ConnectionGene> connectionGenes, final DirectedEdge directedEdge) {
            connectionGenes.remove(directedEdge);

            if (connectionGenes.isEmpty()) {
                return null;
            }

            return connectionGenes;
        }

        private void add(final ConnectionGene connectionGene) {
            Id targetNodeId = connectionGene.getInnovationId().getTargetNodeGeneId();
            Id sourceNodeId = connectionGene.getInnovationId().getSourceNodeGeneId();
            DirectedEdge directedEdge = connectionGene.getInnovationId().getDirectedEdge();

            connectionGenes.put(connectionGene);
            incomingToNodeGeneId.computeIfAbsent(targetNodeId, __ -> new LinkedHashMap<>()).put(directedEdge, connectionGene);
            outgoingFromNodeGeneId.computeIfAbsent(sourceNodeId, __ -> new LinkedHashMap<>()).put(directedEdge, connectionGene);
        }

        private void remove(final ConnectionGene connectionGene) {
            Id targetNodeGeneId = connectionGene.getInnovationId().getTargetNodeGeneId();
            Id sourceNodeGeneId = connectionGene.getInnovationId().getSourceNodeGeneId();
            DirectedEdge directedEdge = connectionGene.getInnovationId().getDirectedEdge();

            connectionGenes.removeByKey(connectionGene.getInnovationId());
            incomingToNodeGeneId.computeIfPresent(targetNodeGeneId, (__, connectionGenes) -> removeIfEmpty(connectionGenes, directedEdge));
            outgoingFromNodeGeneId.computeIfPresent(sourceNodeGeneId, (__, connectionGenes) -> removeIfEmpty(connectionGenes, directedEdge));
        }

        boolean addCyclesAllowed(final ConnectionGene connectionGene, final int delta) {
            boolean previouslyExpressed = connectionGene.isExpressed();

            connectionGene.addCyclesAllowed(delta);

            if (previouslyExpressed && !connectionGene.isExpressed()) {
                remove(connectionGene);
            } else if (!previouslyExpressed && connectionGene.isExpressed()) {
                add(connectionGene);
            }

            return connectionGene.isExpressed();
        }

        ConnectionGene addCyclesAllowed(final int index, final int delta) {
            ConnectionGene connectionGene = getByIndex(index);

            addCyclesAllowed(connectionGene, delta);

            return connectionGene;
        }

        ConnectionGene disableByIndex(final int index) {
            ConnectionGene connectionGene = getByIndex(index);

            addCyclesAllowed(connectionGene, -connectionGene.getCyclesAllowed());

            return connectionGene;
        }
    }
}
