package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface RangeHandler {
    boolean handle(EventLoopId id, int index);
}
