package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultUctCalculator<T extends State> implements UctCalculator<T> {
    private static final float CONSTANT = (float) Math.sqrt(2);
    private final float constant;

    public DefaultUctCalculator() {
        this(CONSTANT);
    }

    @Override
    public float calculate(final int simulations, final Node<T> node) {
        double won = node.getWon();
        double visited = node.getVisited();
        double result = (won / visited) * constant * Math.sqrt(Math.log(simulations) / visited);

        return (float) result;
    }
}
