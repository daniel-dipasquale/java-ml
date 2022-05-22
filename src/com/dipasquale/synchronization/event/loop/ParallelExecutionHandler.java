package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface ParallelExecutionHandler<TProxy, TArgument> {
    void handle(EventLoopId id, TProxy proxy, TArgument argument);
}
