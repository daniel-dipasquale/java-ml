package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AlphaZeroSelectionConfidenceCalculator implements SelectionConfidenceCalculator<AlphaZeroEdge> {
    private final CPuctCalculator cpuctCalculator;

    private static float getExploitationRate(final AlphaZeroEdge edge, final int visited) {
        if (visited == 0) {
            return 0f;
        }

        return edge.getExpectedReward() / (float) visited;
    }

    @Override
    public float calculate(final AlphaZeroEdge edge, final AlphaZeroEdge parentEdge) {
        int visited = edge.getVisited();
        float exploitationRate = getExploitationRate(edge, visited);
        int parentVisited = parentEdge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = edge.getExplorationProbability() * cpuctCalculator.calculate(parentVisited, visited) * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
