package com.dipasquale.ai.common;

@FunctionalInterface
public interface SequentialIdFactory {
    SequentialId next();
}
