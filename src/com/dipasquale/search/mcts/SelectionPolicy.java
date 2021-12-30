package com.dipasquale.search.mcts;

@FunctionalInterface
interface SelectionPolicy<T extends State> {
    SearchNode<T> next(int simulations, SearchNode<T> searchNode);
}
