package com.dipasquale.ai.common;

import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class SequentialIdFactoryStrategy implements SequentialIdFactory {
    @Serial
    private static final long serialVersionUID = -922865675747841717L;
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    @Override
    public SequentialId create() {
        SequentialId sequentialId = sequentialIdFactory.create();

        return new SequentialIdStrategy(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
