package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ExplorationPolicy<T> {
    Node<T> next(Node<T> node, int simulations);
}
