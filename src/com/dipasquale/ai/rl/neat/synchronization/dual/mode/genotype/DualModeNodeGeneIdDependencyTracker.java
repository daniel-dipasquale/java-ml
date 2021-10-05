package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneIdDependencyTracker;
import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeNodeGeneIdDependencyTracker<T extends SetFactory & DualModeObject> implements NodeGeneIdDependencyTracker, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3057494575848684621L;
    private final DualModeIntegerCounter blastRadius;
    private final DualModeSet<DirectedEdge, T> directedEdges;

    public DualModeNodeGeneIdDependencyTracker(final T setFactory) {
        this(new DualModeIntegerCounter(setFactory.concurrencyLevel()), new DualModeSet<>(setFactory));
    }

    public void increaseBlastRadius() {
        blastRadius.increment();
    }

    public int decreaseBlastRadius() { // TODO: assert blastRadius >= 0
        return blastRadius.decrement();
    }

    public void add(final DirectedEdge directedEdge) {
        directedEdges.add(directedEdge);
    }

    public void removeFrom(final Map<DirectedEdge, InnovationId> innovationIds) {
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
