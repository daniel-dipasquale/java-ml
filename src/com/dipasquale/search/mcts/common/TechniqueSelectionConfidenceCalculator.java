package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TechniqueSelectionConfidenceCalculator<T extends TechniqueEdge> implements SelectionConfidenceCalculator<T> {
    private final CPuctCalculator cpuctCalculator;

    private static float getExploitationRate(final TechniqueEdge edge, final int visited) {
        if (visited == 0) {
            return 0f;
        }

        return edge.getExpectedReward() / (float) visited;
    }

    @Override
    public float calculate(final TechniqueEdge edge, final TechniqueEdge parentEdge) {
        int visited = edge.getVisited();
        float exploitationRate = getExploitationRate(edge, visited);
        int parentVisited = parentEdge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = edge.getExplorationProbability() * cpuctCalculator.calculate(parentVisited, visited) * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
