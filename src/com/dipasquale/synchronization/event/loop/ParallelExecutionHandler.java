package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface ParallelExecutionHandler<TProxy, TArgument> {
    void handle(TProxy proxy, TArgument argument);
}
