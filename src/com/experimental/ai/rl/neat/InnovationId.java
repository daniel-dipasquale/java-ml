package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public final class InnovationId<T extends Comparable<T>> implements Comparable<InnovationId<T>> {
    @Getter
    @EqualsAndHashCode.Include
    private final DirectedEdge<T> directedEdge;
    private final T index;

    public T getInNodeId() {
        return directedEdge.getInNodeId();
    }

    public T getOutNodeId() {
        return directedEdge.getOutNodeId();
    }

    @Override
    public int compareTo(final InnovationId<T> other) {
        return index.compareTo(other.index);
    }
}
