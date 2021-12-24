package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SelectionPolicy<T extends State> {
    float calculateConfidence(int simulations, SearchNode<T> searchNode);
}
