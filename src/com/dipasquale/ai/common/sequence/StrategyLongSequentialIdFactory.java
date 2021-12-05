package com.dipasquale.ai.common.sequence;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyLongSequentialIdFactory implements SequentialIdFactory<StrategyLongSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -922865675747841717L;
    private final String name;
    private final LongSequentialIdFactory sequentialIdFactory;

    @Override
    public StrategyLongSequentialId create() {
        LongSequentialId sequentialId = sequentialIdFactory.create();

        return new StrategyLongSequentialId(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
