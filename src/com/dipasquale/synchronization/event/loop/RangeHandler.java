package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface RangeHandler {
    boolean handle(int index);
}
