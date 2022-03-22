package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;

@FunctionalInterface
public interface ExplorationProbabilityCalculator<T extends Action> {
    float calculate(T action);
}
