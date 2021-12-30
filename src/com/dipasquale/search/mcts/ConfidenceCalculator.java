package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ConfidenceCalculator<T extends State> {
    float calculate(int simulations, SearchNode<T> searchNode);
}
