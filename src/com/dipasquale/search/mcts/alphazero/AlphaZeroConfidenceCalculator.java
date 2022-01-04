package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AlphaZeroConfidenceCalculator implements ConfidenceCalculator<AlphaZeroSearchEdge> {
    private final CPuctAlgorithm cpuct;

    @Override
    public float calculate(final int simulations, final AlphaZeroSearchEdge edge) {
        float exploitationRate = edge.getExpectedReward();
        int visited = edge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = cpuct.getValue(simulations, visited) * edge.getExplorationProbability() * (float) (Math.sqrt(simulations) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
