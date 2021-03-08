package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private final boolean recurrentConnectionsAllowed;
    private final SequentialIdFactory innovationIdFactory;
    private final ConnectionGeneWeightFactory weightFactory;
    private final Map<DirectedEdge, InnovationId> innovationIds;
    private final Set<DirectedEdge> innovationIdsNotAllowed;

    @Override
    public boolean recurrentConnectionsAllowed() {
        return recurrentConnectionsAllowed;
    }

    @Override
    public InnovationId getOrCreateInnovationId(final DirectedEdge directedEdge) {
        if (recurrentConnectionsAllowed) {
            return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId(de, innovationIdFactory.next()));
        }

        if (innovationIdsNotAllowed.contains(directedEdge)) {
            return null;
        }

        innovationIdsNotAllowed.add(directedEdge.createReversed());

        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId(de, innovationIdFactory.next()));
    }

    @Override
    public float nextWeight() {
        return weightFactory.next();
    }
}
