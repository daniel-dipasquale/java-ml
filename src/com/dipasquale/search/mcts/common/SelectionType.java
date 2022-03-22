package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SelectionType {
    INTENTIONAL_ONLY,
    MIXED;

    public static <T extends Action> SelectionType determine(final ExplorationProbabilityCalculator<T> explorationProbabilityCalculator) {
        if (explorationProbabilityCalculator == null) {
            return INTENTIONAL_ONLY;
        }

        return MIXED;
    }
}
