package com.dipasquale.search.mcts;

@FunctionalInterface
interface ExplorationPolicy<T extends State> {
    SearchNode<T> next(int simulations, SearchNode<T> searchNode);
}
