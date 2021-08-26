package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
public interface EventLoopHandler {
    void handle(String name);
}
