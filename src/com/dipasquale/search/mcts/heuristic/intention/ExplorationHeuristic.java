package com.dipasquale.search.mcts.heuristic.intention;

@FunctionalInterface
public interface ExplorationHeuristic<T> {
    float estimate(T action);
}
