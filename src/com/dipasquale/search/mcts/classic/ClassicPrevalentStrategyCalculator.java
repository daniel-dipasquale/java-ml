package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.StrategyCalculator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class ClassicPrevalentStrategyCalculator implements StrategyCalculator<ClassicSearchEdge> {
    private final float winningFactor;
    private final float notLosingFactor;

    @Override
    public float calculateEfficiency(final ClassicSearchEdge edge) {
        float visited = (float) edge.getVisited();
        float won = (float) edge.getWon();
        float drawn = (float) edge.getDrawn();
        float wonRate = won / visited;
        float notLostRate = (won + drawn) / visited;

        return winningFactor * wonRate + notLosingFactor * notLostRate;
    }
}
