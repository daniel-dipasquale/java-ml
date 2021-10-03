package com.dipasquale.ai.rl.neat.synchronization.dual.mode.genotype;

import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.ai.common.sequence.StrategySequentialIdFactory;
import com.dipasquale.synchronization.dual.mode.DualModeLongCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeSequentialIdFactory implements SequentialIdFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8362275222819817723L;
    private final DualModeLongCounter id;
    private final SequentialIdFactory sequentialIdFactory;

    private DualModeSequentialIdFactory(final DualModeLongCounter id, final String name) {
        this.id = id;
        this.sequentialIdFactory = new StrategySequentialIdFactory(name, new LongSequentialIdFactory(id));
    }

    public DualModeSequentialIdFactory(final int concurrencyLevel, final String name) {
        this(new DualModeLongCounter(concurrencyLevel), name);
    }

    @Override
    public SequentialId create() {
        return sequentialIdFactory.create();
    }

    @Override
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
