package com.dipasquale.synchronization.dual.mode;

@FunctionalInterface
public interface DualModeFactory<T> {
    T create(ConcurrencyLevelState concurrencyLevelState);
}
