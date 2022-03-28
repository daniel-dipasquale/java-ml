package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ExpectedRewardActionEfficiencyCalculator implements ActionEfficiencyCalculator<HeuristicEdge> {
    private static final ExpectedRewardActionEfficiencyCalculator INSTANCE = new ExpectedRewardActionEfficiencyCalculator();

    public static ExpectedRewardActionEfficiencyCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public float calculate(final int depth, final HeuristicEdge edge) {
        return edge.calculateExpectedReward();
    }
}
