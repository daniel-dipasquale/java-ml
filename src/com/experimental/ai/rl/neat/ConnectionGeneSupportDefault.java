package com.experimental.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import com.experimental.ai.common.Counter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ConnectionGeneSupportDefault<T extends Comparable<T>> implements Context.ConnectionGeneSupport<T> {
    private final Counter<T> counter;
    private final RandomSupportFloat randomSupport;
    private final Map<DirectedEdge<T>, InnovationId<T>> innovationIds;
    private final boolean allowCyclicConnections;
    private final boolean allowReInnovations;

    @Override
    public boolean allowCyclicConnections() {
        return allowCyclicConnections;
    }

    @Override
    public boolean allowReInnovations() {
        return allowReInnovations;
    }

    @Override
    public InnovationId<T> getInnovationId(final DirectedEdge<T> directedEdge) {
        return innovationIds.get(directedEdge);
    }

    @Override
    public InnovationId<T> createInnovationId(final DirectedEdge<T> directedEdge) {
        return innovationIds.computeIfAbsent(directedEdge, de -> new InnovationId<>(de, counter.next()));
    }

    @Override
    public float nextWeight() {
        return randomSupport.next() * 4f - 2f;
    }
}
