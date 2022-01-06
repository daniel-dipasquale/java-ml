package com.dipasquale.ai.common.sequence;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyNumberSequentialIdFactory implements SequentialIdFactory<StrategyNumberSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -922865675747841717L;
    private final String name;
    private final NumberSequentialIdFactory sequentialIdFactory;

    @Override
    public StrategyNumberSequentialId create() {
        NumberSequentialId sequentialId = sequentialIdFactory.create();

        return new StrategyNumberSequentialId(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
