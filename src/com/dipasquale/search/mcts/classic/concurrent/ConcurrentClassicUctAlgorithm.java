package com.dipasquale.search.mcts.classic.concurrent;

import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConcurrentClassicUctAlgorithm implements UctAlgorithm<ConcurrentClassicEdge> {
    private static final double CONSTANT = Math.sqrt(2D);
    private final int maximumSelectionCount;
    private final double constant;

    public ConcurrentClassicUctAlgorithm(final int maximumSelectionCount) {
        this(maximumSelectionCount, CONSTANT);
    }

    @Override
    public float calculate(final ConcurrentClassicEdge edge, final ConcurrentClassicEdge parentEdge) {
        double visited = edge.getVisited() + edge.getSelectionCount();

        if (Double.compare(visited, 0D) == 0) {
            return 0f;
        }

        double won = edge.getWon();
        double parentVisited = parentEdge.getVisited() + maximumSelectionCount;
        double exploitationRate = (won / visited);
        double explorationRate = constant * Math.sqrt(Math.log(parentVisited) / visited);

        return (float) (exploitationRate + explorationRate);
    }
}
