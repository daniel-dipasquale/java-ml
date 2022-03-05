package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SelectionConfidenceCalculator<T extends Edge> {
    float calculate(T edge);
}
