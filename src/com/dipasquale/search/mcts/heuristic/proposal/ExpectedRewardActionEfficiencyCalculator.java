package com.dipasquale.search.mcts.heuristic.proposal;

import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import com.dipasquale.search.mcts.proposal.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpectedRewardActionEfficiencyCalculator<T extends HeuristicEdge> implements ActionEfficiencyCalculator<T> {
    private static final ExpectedRewardActionEfficiencyCalculator<?> INSTANCE = new ExpectedRewardActionEfficiencyCalculator<>();

    public static <T extends HeuristicEdge> ExpectedRewardActionEfficiencyCalculator<T> getInstance() {
        return (ExpectedRewardActionEfficiencyCalculator<T>) INSTANCE;
    }

    @Override
    public float calculate(final int depth, final T edge) {
        return edge.calculateExpectedReward();
    }
}
