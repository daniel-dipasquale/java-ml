package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.IntegerValue;
import com.dipasquale.common.StandardIntegerValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeGeneDependencyTracker implements Serializable {
    @Serial
    private static final long serialVersionUID = -3057494575848684621L;
    private final IntegerValue blastRadius = new StandardIntegerValue(0);
    private final Set<DirectedEdge> directedEdges = new HashSet<>();

    public void increaseBlastRadius() {
        blastRadius.increment();
    }

    public int decreaseBlastRadius() {
        return blastRadius.decrement();
    }

    public void addEdge(final DirectedEdge directedEdge) {
        directedEdges.add(directedEdge);
    }

    public void removeEdgesFrom(final Map<DirectedEdge, InnovationId> innovationIds) {
        for (DirectedEdge directedEdge : directedEdges) {
            innovationIds.remove(directedEdge);
        }
    }
}
