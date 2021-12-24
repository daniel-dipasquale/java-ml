package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UctSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private static final double CONSTANT = Math.sqrt(2);
    private final double constant;

    public UctSelectionPolicy() {
        this(CONSTANT);
    }

    @Override
    public float calculateConfidence(final int simulations, final SearchNode<T> searchNode) {
        double won = searchNode.getWon();
        double visited = searchNode.getVisited();
        double result = (won / visited) + constant * Math.sqrt(Math.log(simulations) / visited);

        return (float) result;
    }
}
