package com.dipasquale.search.mcts.core;

public final class MostVisitedStrategyCalculator<T extends Edge> implements StrategyCalculator<T> {
    @Override
    public float calculateEfficiency(final T edge) {
        return (float) edge.getVisited();
    }
}
