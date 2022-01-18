package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface EdgeFactory<T extends Edge> {
    T create(T parent);
}
