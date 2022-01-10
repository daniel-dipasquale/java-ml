package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface ConfidenceCalculator<T extends Edge> {
    float calculate(int simulations, T edge);
}
