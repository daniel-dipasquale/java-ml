package com.dipasquale.search.mcts;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class DefaultStrategyCalculator<T extends State> implements StrategyCalculator<T> {
    private final float winningFactor;
    private final float notLosingFactor;

    @Override
    public float calculateEfficiency(final SearchNode<T> searchNode) {
        float visited = (float) searchNode.getVisited();
        float won = (float) searchNode.getWon();
        float drawn = (float) searchNode.getDrawn();
        float wonRate = won / visited;
        float notLostRate = (won + drawn) / visited;

        return winningFactor * wonRate + notLosingFactor * notLostRate;
    }
}
