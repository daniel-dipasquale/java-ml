package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultStrategyCalculator<T extends State> implements StrategyCalculator<T> {
    private final float winningFactor;
    private final float notLosingFactor;

    @Override
    public float calculateEfficiency(final Node<T> node) {
        float visited = (float) node.getVisited();
        float won = (float) node.getWon();
        float drawn = (float) node.getDrawn();

        return winningFactor * won / visited + notLosingFactor * ((won + drawn) / visited);
    }
}
