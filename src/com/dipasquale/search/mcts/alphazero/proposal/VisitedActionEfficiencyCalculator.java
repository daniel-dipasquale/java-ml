package com.dipasquale.search.mcts.alphazero.proposal;

import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.proposal.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitedActionEfficiencyCalculator implements ActionEfficiencyCalculator<AlphaZeroEdge> {
    private static final VisitedActionEfficiencyCalculator INSTANCE = new VisitedActionEfficiencyCalculator();

    public static VisitedActionEfficiencyCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public float calculate(final int depth, final AlphaZeroEdge edge) {
        return (float) edge.getVisited();
    }
}
