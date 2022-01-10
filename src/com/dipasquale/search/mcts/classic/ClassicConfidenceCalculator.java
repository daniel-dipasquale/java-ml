package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicConfidenceCalculator implements ConfidenceCalculator<ClassicEdge> {
    private static final double CONSTANT = Math.sqrt(2D);
    private final double constant;

    public ClassicConfidenceCalculator() {
        this(CONSTANT);
    }

    @Override
    public float calculate(final int simulations, final ClassicEdge edge) {
        double won = edge.getWon();
        double visited = edge.getVisited() - edge.getUnfinished();
        double result = (won / visited) + constant * Math.sqrt(Math.log(simulations) / visited);

        return (float) result;
    }
}
