package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.StrategyLongSequentialIdFactory;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.synchronization.dual.mode.DualModeLongCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeIdFactory implements ObjectFactory<Id>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8362275222819817723L;
    private final DualModeLongCounter id;
    private final StrategyLongSequentialIdFactory sequentialIdFactory;

    private DualModeIdFactory(final DualModeLongCounter id, final String name) {
        this.id = id;
        this.sequentialIdFactory = new StrategyLongSequentialIdFactory(name, new LongSequentialIdFactory(id));
    }

    public DualModeIdFactory(final int concurrencyLevel, final String name) {
        this(new DualModeLongCounter(concurrencyLevel), name);
    }

    @Override
    public Id create() {
        return new Id(sequentialIdFactory.create());
    }

    public void reset() {
        sequentialIdFactory.reset();
    }

    @Override
    public int concurrencyLevel() {
        return id.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        id.activateMode(concurrencyLevel);
    }
}
