package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface SearchEdgeFactory<T extends SearchEdge> {
    T create(T parent);
}
