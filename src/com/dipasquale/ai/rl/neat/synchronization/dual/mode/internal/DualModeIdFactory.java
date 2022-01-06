package com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal;

import com.dipasquale.ai.common.sequence.NumberSequentialIdFactory;
import com.dipasquale.ai.common.sequence.StrategyNumberSequentialIdFactory;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.synchronization.dual.mode.DualModeLongValue;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeIdFactory implements ObjectFactory<Id>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8362275222819817723L;
    private final DualModeLongValue id;
    private final StrategyNumberSequentialIdFactory sequentialIdFactory;

    private DualModeIdFactory(final DualModeLongValue id, final String name) {
        this.id = id;
        this.sequentialIdFactory = new StrategyNumberSequentialIdFactory(name, new NumberSequentialIdFactory(id));
    }

    public DualModeIdFactory(final int concurrencyLevel, final IdType idType) {
        this(new DualModeLongValue(concurrencyLevel), idType.getName());
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
