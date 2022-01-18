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
    public float calculate(final ClassicEdge edge) {
        double won = edge.getWon();
        ClassicEdge parentEdge = edge.getParent();
        double parentVisited = parentEdge.getVisited() - parentEdge.getUnfinished();
        double visited = edge.getVisited() - edge.getUnfinished();
        double result = (won / visited) + constant * Math.sqrt(Math.log(parentVisited) / visited);

        return (float) result;
    }
}
