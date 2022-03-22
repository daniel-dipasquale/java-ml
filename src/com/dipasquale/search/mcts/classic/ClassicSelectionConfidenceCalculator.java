package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ClassicSelectionConfidenceCalculator implements SelectionConfidenceCalculator<ClassicEdge> {
    private static final double CONSTANT = Math.sqrt(2D);
    private final double constant;

    ClassicSelectionConfidenceCalculator() {
        this(CONSTANT);
    }

    @Override
    public float calculate(final ClassicEdge edge, final ClassicEdge parentEdge) {
        double visited = edge.getVisited();

        if (Double.compare(visited, 0D) == 0) {
            return 0f;
        }

        double won = edge.getWon();
        double parentVisited = parentEdge.getVisited();
        double exploitationRate = (won / visited);
        double explorationRate = constant * Math.sqrt(Math.log(parentVisited) / visited);

        return (float) (exploitationRate + explorationRate);
    }
}
