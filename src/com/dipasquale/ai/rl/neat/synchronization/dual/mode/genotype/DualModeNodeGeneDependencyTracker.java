package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneDependencyTracker;
import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerValue;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeNodeGeneDependencyTracker<T extends SetFactory & DualModeObject> implements NodeGeneDependencyTracker, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3057494575848684621L;
    private final DualModeIntegerValue blastRadius;
    private final DualModeSet<DirectedEdge, T> directedEdges;

    public DualModeNodeGeneDependencyTracker(final T setFactory) {
        this(new DualModeIntegerValue(setFactory.concurrencyLevel(), 0), new DualModeSet<>(setFactory));
    }

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

    @Override
    public int concurrencyLevel() {
        return blastRadius.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        blastRadius.activateMode(concurrencyLevel);
        directedEdges.activateMode(concurrencyLevel);
    }
}
