package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface StrategyCalculator<T extends SearchEdge> {
    float calculateEfficiency(T edge);
}
