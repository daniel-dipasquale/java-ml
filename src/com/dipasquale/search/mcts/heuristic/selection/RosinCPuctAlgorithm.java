package com.dipasquale.search.mcts.heuristic.selection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RosinCPuctAlgorithm implements CPuctAlgorithm {
    private static final double DEFAULT_BASE = 19_652D;
    private static final double DEFAULT_INIT = 2.5D;
    private final double base;
    private final double init;

    public RosinCPuctAlgorithm() {
        this(DEFAULT_BASE, DEFAULT_INIT);
    }

    @Override
    public float calculate(final int simulations, final int visited) {
        double visitedPlusOne = visited + 1;
        double result = Math.log10((base + visitedPlusOne) / base) + init;

        return (float) result;
    }
}
