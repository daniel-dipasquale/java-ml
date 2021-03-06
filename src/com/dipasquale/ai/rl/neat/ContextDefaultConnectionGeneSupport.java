package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultConnectionGeneSupport<T extends Comparable<T>> implements Context.ConnectionGeneSupport<T> {
    private final boolean allowRecurrentConnections;
    private final SequentialIdFactory<T> innovationIdFactory;
    private final ConnectionGeneWeightFactory weightFactory;
    private final Map<DirectedEdge<T>, InnovationId<T>> innovationIds;
    private final Set<DirectedEdge<T>> innovationIdsNotAllowed;

    @Override
    public boolean allowRecurrentConnections() {
        return allowRecurrentConnections;
    }

    @Override
    public InnovationId<T> getOrCreateInnovationId(final DirectedEdge<T> directedEdge) {
        if (allowRecurrentConnections) {
            return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId<>(de, innovationIdFactory.next()));
        }

        if (innovationIdsNotAllowed.contains(directedEdge)) {
            return null;
        }

        innovationIdsNotAllowed.add(directedEdge.createReversed());

        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId<>(de, innovationIdFactory.next()));
    }

    @Override
    public float nextWeight() {
        return weightFactory.next();
    }
}
