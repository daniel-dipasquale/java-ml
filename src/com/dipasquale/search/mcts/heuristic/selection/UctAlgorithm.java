package com.dipasquale.search.mcts.heuristic.selection;

import com.dipasquale.search.mcts.Edge;

@FunctionalInterface
public interface UctAlgorithm<T extends Edge> {
    float calculate(T edge, T parentEdge);
}
