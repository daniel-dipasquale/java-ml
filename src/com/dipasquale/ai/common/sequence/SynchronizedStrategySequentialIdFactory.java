package com.dipasquale.ai.common.sequence;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class SynchronizedStrategySequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -6141737126849114387L;
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    private SequentialId createSequentialId() {
        synchronized (sequentialIdFactory) {
            return sequentialIdFactory.create();
        }
    }

    @Override
    public SequentialId create() {
        SequentialId sequentialId = createSequentialId();

        return new StrategySequentialId(name, sequentialId);
    }

    @Override
    public void reset() {
        synchronized (sequentialIdFactory) {
            sequentialIdFactory.reset();
        }
    }
}
