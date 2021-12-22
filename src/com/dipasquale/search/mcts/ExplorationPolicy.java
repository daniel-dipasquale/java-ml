package com.dipasquale.search.mcts;

@FunctionalInterface
interface ExplorationPolicy<T extends State> {
    Node<T> next(Node<T> node, int simulations);
}
