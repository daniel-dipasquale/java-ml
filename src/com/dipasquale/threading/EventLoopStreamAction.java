package com.dipasquale.threading;

@FunctionalInterface
public interface EventLoopStreamAction<T> {
    void enact(T item);
}
