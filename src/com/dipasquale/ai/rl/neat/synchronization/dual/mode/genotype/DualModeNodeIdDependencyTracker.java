package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneIdDependencyTracker;
import com.dipasquale.synchronization.dual.mode.DualModeIntegerCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeNodeIdDependencyTracker implements NodeGeneIdDependencyTracker, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -3057494575848684621L;
    private final DualModeIntegerCounter blastRadius;
    private final DualModeSet<DirectedEdge> directedEdges;

    public DualModeNodeIdDependencyTracker(final boolean concurrent, final int numberOfThreads, final DualModeNodeIdDependencyTracker other) {
        this(new DualModeIntegerCounter(concurrent), new DualModeSet<>(concurrent, numberOfThreads, other.directedEdges));
    }

    public DualModeNodeIdDependencyTracker(final boolean concurrent, final int numberOfThreads) {
        this(new DualModeIntegerCounter(concurrent), new DualModeSet<>(concurrent, numberOfThreads));
    }

    public void increaseBlastRadius() {
        blastRadius.increment();
    }

    public int decreaseBlastRadius() {
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
    public void switchMode(final boolean concurrent) {
        blastRadius.switchMode(concurrent);
        directedEdges.switchMode(concurrent);
    }
}
