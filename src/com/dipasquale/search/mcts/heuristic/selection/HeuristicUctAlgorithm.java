package com.dipasquale.search.mcts.heuristic.selection;

import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HeuristicUctAlgorithm<T extends HeuristicEdge> implements UctAlgorithm<T> {
    private final CPuctAlgorithm cpuctAlgorithm;

    @Override
    public float calculate(final HeuristicEdge edge, final HeuristicEdge parentEdge) {
        float exploitationRate = edge.calculateExpectedReward();
        int parentVisited = parentEdge.getVisited();
        int visited = edge.getVisited();
        double visitedPlusOne = visited + 1;
        float explorationRate = edge.getExplorationProbability() * cpuctAlgorithm.calculate(parentVisited, visited) * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
