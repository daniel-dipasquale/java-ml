package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicSelectionConfidenceCalculator implements SelectionConfidenceCalculator<ClassicEdge> {
    private static final double CONSTANT = Math.sqrt(2D);
    private final double constant;

    public ClassicSelectionConfidenceCalculator() {
        this(CONSTANT);
    }

    @Override
    public float calculate(final ClassicEdge edge, final ClassicEdge parentEdge) {
        double won = edge.getWon();
        double parentVisited = parentEdge.getVisited() - parentEdge.getUnfinished();
        double visited = edge.getVisited() - edge.getUnfinished();
        double result = (won / visited) + constant * Math.sqrt(Math.log(parentVisited) / visited);

        return (float) result;
    }
}
