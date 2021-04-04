package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SequentialIdFactoryStrategySynchronized implements SequentialIdFactory {
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    private SequentialId createSequentialId() {
        synchronized (sequentialIdFactory) {
            return sequentialIdFactory.next();
        }
    }

    @Override
    public SequentialId next() {
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
