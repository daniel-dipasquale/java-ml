package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UctConfidenceCalculator<T extends State> implements ConfidenceCalculator<T> {
    private static final double CONSTANT = Math.sqrt(2);
    private final double constant;

    public UctConfidenceCalculator() {
        this(CONSTANT);
    }

    @Override
    public float calculate(final int simulations, final SearchNode<T> searchNode) {
        double won = searchNode.getWon();
        double visited = searchNode.getVisited();
        double result = (won / visited) + constant * Math.sqrt(Math.log(simulations) / visited);

        return (float) result;
    }
}
