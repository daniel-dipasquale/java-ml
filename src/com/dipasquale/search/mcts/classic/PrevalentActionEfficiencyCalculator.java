package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class PrevalentActionEfficiencyCalculator<T extends Action> implements ActionEfficiencyCalculator<T, ClassicEdge> {
    private final float winningFactor;
    private final float notLosingFactor;

    @Override
    public float calculate(final int depth, final T action, final ClassicEdge edge) {
        float visited = (float) edge.getVisited();
        float won = (float) edge.getWon();
        float drawn = (float) edge.getDrawn();
        float wonRate = won / visited;
        float notLostRate = (won + drawn) / visited;

        return winningFactor * wonRate + notLosingFactor * notLostRate;
    }
}
