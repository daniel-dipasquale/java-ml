package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ActionEfficiencyCalculator<T extends Edge> {
    float calculate(int depth, T edge);
}
