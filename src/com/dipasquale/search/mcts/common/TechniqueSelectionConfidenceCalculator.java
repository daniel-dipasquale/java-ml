package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TechniqueSelectionConfidenceCalculator<T extends TechniqueEdge> implements SelectionConfidenceCalculator<T> {
    private final CPuctCalculator cpuctCalculator;

    @Override
    public float calculate(final TechniqueEdge edge, final TechniqueEdge parentEdge) {
        float exploitationRate = edge.calculateExpectedReward();
        int parentVisited = parentEdge.getVisited();
        int visited = edge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = edge.getExplorationProbability() * cpuctCalculator.calculate(parentVisited, visited) * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
