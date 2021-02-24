package com.experimental.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import com.experimental.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ConnectionGeneSupportDefault<T extends Comparable<T>> implements Context.ConnectionGeneSupport<T> {
    private final boolean allowCyclicConnections;
    private final SequentialIdFactory<T> sequentialIdFactory;
    private final RandomSupportFloat randomSupportFloat;
    private final Map<DirectedEdge<T>, InnovationId<T>> innovationIds;
    private final Set<DirectedEdge<T>> innovationIdsNotAllowed;

    @Override
    public boolean allowCyclicConnections() {
        return allowCyclicConnections;
    }

    @Override
    public InnovationId<T> getOrCreateInnovationId(final DirectedEdge<T> directedEdge) {
        if (allowCyclicConnections) {
            return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId<>(de, sequentialIdFactory.next()));
        }

        if (innovationIdsNotAllowed.contains(directedEdge)) {
            return null;
        }

        innovationIdsNotAllowed.add(directedEdge.createReversed());

        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId<>(de, sequentialIdFactory.next()));
    }

    @Override
    public float nextWeight() {
        return randomSupportFloat.next();
    }
}
