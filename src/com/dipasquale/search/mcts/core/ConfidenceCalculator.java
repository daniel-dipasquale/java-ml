package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface ConfidenceCalculator<T extends SearchEdge> {
    float calculate(int simulations, T edge);
}
