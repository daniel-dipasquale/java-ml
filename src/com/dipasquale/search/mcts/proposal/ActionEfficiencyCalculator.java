package com.dipasquale.search.mcts.proposal;

import com.dipasquale.search.mcts.Edge;

@FunctionalInterface
public interface ActionEfficiencyCalculator<T extends Edge> {
    float calculate(int depth, T edge);
}
