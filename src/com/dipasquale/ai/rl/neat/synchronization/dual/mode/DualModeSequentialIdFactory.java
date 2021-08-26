package com.dipasquale.ai.rl.neat.synchronization.dual.mode;

import com.dipasquale.ai.common.sequence.LongSequentialIdFactory;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialIdFactory;
import com.dipasquale.ai.common.sequence.StrategySequentialIdFactory;
import com.dipasquale.synchronization.dual.mode.DualModeLongCounter;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualModeSequentialIdFactory implements SequentialIdFactory, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -8362275222819817723L;
    private final DualModeLongCounter id;
    private final SequentialIdFactory sequentialIdFactory;

    private DualModeSequentialIdFactory(final String name, final DualModeLongCounter id) {
        this(id, new StrategySequentialIdFactory(name, new LongSequentialIdFactory(id)));
    }

    public DualModeSequentialIdFactory(final boolean concurrent, final String name) {
        this(name, new DualModeLongCounter(concurrent));
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
    public void switchMode(boolean concurrent) {
        id.switchMode(concurrent);
    }
}
