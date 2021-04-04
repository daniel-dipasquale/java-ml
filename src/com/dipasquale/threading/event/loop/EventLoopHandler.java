package com.dipasquale.threading.event.loop;

@FunctionalInterface
public interface EventLoopHandler {
    void handle(String name);
}
