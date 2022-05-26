package com.dipasquale.search.mcts.classic.selection;

import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicUctAlgorithm<T extends ClassicEdge> implements UctAlgorithm<T> {
    private static final double CONSTANT = Math.sqrt(2D);
    private static final ClassicUctAlgorithm<?> INSTANCE = new ClassicUctAlgorithm<>();
    private final double constant;

    private ClassicUctAlgorithm() {
        this(CONSTANT);
    }

    public static <T extends ClassicEdge> ClassicUctAlgorithm<T> getInstance() {
        return (ClassicUctAlgorithm<T>) INSTANCE;
    }

    @Override
    public float calculate(final T edge, final T parentEdge) {
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
