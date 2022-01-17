package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeMapToSetFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.set.DualModeSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class DualModeNodeGeneDependencyTrackerFactory implements ObjectFactory<DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory>>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 107984128175600587L;
    @Setter(AccessLevel.PRIVATE)
    private int concurrencyLevel;
    private final DualModeMapToSetFactory setFactory;

    @Override
    public DualModeNodeGeneDependencyTracker<DualModeMapToSetFactory> create() {
        return new DualModeNodeGeneDependencyTracker<>(concurrencyLevel, new DualModeSet<>(setFactory));
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        setConcurrencyLevel(concurrencyLevel);
        setFactory.activateMode(concurrencyLevel);
    }
}
