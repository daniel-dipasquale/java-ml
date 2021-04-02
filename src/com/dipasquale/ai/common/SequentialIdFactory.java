package com.dipasquale.ai.common;

public interface SequentialIdFactory {
    SequentialId next();

    void reset();
}
