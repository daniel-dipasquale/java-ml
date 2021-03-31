package com.dipasquale.threading;

@FunctionalInterface
public interface EventLoopHandler {
    void handle(String name);
}
