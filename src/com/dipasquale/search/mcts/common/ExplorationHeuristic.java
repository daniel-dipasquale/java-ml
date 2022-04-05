package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;

@FunctionalInterface
public interface ExplorationHeuristic<T extends Action> {
    float estimate(T action);
}
