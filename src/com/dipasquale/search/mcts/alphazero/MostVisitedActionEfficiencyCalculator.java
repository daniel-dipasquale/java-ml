package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MostVisitedActionEfficiencyCalculator<T extends Action> implements ActionEfficiencyCalculator<T, AlphaZeroEdge> {
    private static final MostVisitedActionEfficiencyCalculator<?> INSTANCE = new MostVisitedActionEfficiencyCalculator<>();

    public static <T extends Action> MostVisitedActionEfficiencyCalculator<T> getInstance() {
        return (MostVisitedActionEfficiencyCalculator<T>) INSTANCE;
    }

    @Override
    public float calculate(final int depth, final T action, final AlphaZeroEdge edge) {
        return (float) edge.getVisited();
    }
}
