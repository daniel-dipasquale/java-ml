package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface StrategyCalculator<T extends Edge> {
    float calculateEfficiency(T edge);
}
