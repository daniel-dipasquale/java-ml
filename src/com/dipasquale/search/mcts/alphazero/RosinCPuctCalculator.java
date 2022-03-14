package com.dipasquale.search.mcts.alphazero;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RosinCPuctCalculator implements CPuctCalculator {
    private static final double DEFAULT_BASE = 19_652D;
    private static final double DEFAULT_INIT = 2.5D;
    private final double base;
    private final double init;

    public RosinCPuctCalculator() {
        this(DEFAULT_BASE, DEFAULT_INIT);
    }

    @Override
    public float calculate(final int simulations, final int visited) {
        double visitedPlusOne = visited + 1;
        double result = Math.log10((base + visitedPlusOne) / base) + init;

        return (float) result;
    }
}
