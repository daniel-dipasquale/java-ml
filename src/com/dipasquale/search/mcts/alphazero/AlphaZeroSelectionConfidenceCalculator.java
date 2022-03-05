package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AlphaZeroSelectionConfidenceCalculator implements SelectionConfidenceCalculator<AlphaZeroEdge> {
    private final CPuctAlgorithm cpuct;

    @Override
    public float calculate(final AlphaZeroEdge edge) {
        float exploitationRate = edge.getExpectedReward();
        int parentVisited = edge.getParent().getVisited();
        int visited = edge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = cpuct.getValue(parentVisited, visited) * edge.getExplorationProbability() * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
