/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.sequence;

import java.io.Serial;
import java.io.Serializable;

public final class DefaultSequentialIdFactory implements SequentialIdFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private long current;

    public DefaultSequentialIdFactory() {
        this.current = 0L;
    }

    public DefaultSequentialIdFactory(final DefaultSequentialIdFactory other) {
        this.current = other.current;
    }

    @Override
    public SequentialId create() {
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        long value = ++current;

        return new DefaultSequentialId(value);
    }

    @Override
    public void reset() {
        current = 0L;
    }
}
