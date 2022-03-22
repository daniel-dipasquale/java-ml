package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class VisitedActionEfficiencyCalculator implements ActionEfficiencyCalculator<AlphaZeroEdge> {
    private static final VisitedActionEfficiencyCalculator INSTANCE = new VisitedActionEfficiencyCalculator();

    public static VisitedActionEfficiencyCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public float calculate(final int depth, final AlphaZeroEdge edge) {
        return (float) edge.getVisited();
    }
}
