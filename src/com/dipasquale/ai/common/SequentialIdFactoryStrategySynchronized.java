package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class SequentialIdFactoryStrategySynchronized implements SequentialIdFactory {
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

        return new SequentialIdStrategy(name, sequentialId);
    }

    @Override
    public void reset() {
        synchronized (sequentialIdFactory) {
            sequentialIdFactory.reset();
        }
    }
}
