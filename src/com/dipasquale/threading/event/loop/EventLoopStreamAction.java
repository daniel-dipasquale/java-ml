package com.dipasquale.threading.event.loop;

@FunctionalInterface
public interface EventLoopStreamAction<T> {
    void enact(T item);
}
