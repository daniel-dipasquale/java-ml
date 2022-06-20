package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface ParallelExecutionProxyFactory<T> {
    T create(int workerId, int count);
}
