/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.sequence;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategySequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -922865675747841717L;
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    @Override
    public SequentialId create() {
        SequentialId sequentialId = sequentialIdFactory.create();

        return new StrategySequentialId(name, sequentialId);
    }

    @Override
    public void reset() {
        sequentialIdFactory.reset();
    }
}
