package com.dipasquale.ai.common.sequence;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class NamedLongSequentialIdFactory implements SequentialIdFactory<NamedLongSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -922865675747841717L;
    private final String name;
    private final LongSequentialIdFactory sequentialIdFactory;

    @Override
    public NamedLongSequentialId create() {
        LongSequentialId sequentialId = sequentialIdFactory.create();

        return new NamedLongSequentialId(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
