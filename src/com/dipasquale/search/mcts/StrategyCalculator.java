package com.dipasquale.search.mcts;

@FunctionalInterface
public interface StrategyCalculator<T extends State> {
    float calculateEfficiency(SearchNode<T> searchNode);
}
