package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SequentialIdFactoryStrategy implements SequentialIdFactory {
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    @Override
    public SequentialId next() {
        SequentialId sequentialId = sequentialIdFactory.next();

        return new SequentialIdStrategy(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
