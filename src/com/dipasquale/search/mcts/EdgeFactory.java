package com.dipasquale.search.mcts;

@FunctionalInterface
public interface EdgeFactory<T extends Edge> {
    T create(SearchNode<?, T, ?> node);
}
