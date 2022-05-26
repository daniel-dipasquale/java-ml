package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConcurrentHeuristicUctAlgorithm<T extends HeuristicEdge & ConcurrentEdge> implements UctAlgorithm<T> {
    private final int maximumSelectionCount;
    private final CPuctAlgorithm cpuctAlgorithm;

    @Override
    public float calculate(final T edge, final T parentEdge) {
        int selectionCount = edge.getSelectionCount();
        float exploitationRate = edge.calculateExpectedReward(selectionCount, (float) -selectionCount);
        int parentVisited = parentEdge.getVisited() + maximumSelectionCount;
        int visited = edge.getVisited() + selectionCount;
        double visitedPlusOne = visited + 1;
        float explorationRate = edge.getExplorationProbability() * cpuctAlgorithm.calculate(parentVisited, visited) * (float) (Math.sqrt(parentVisited) / visitedPlusOne);

        return exploitationRate + explorationRate;
    }
}
