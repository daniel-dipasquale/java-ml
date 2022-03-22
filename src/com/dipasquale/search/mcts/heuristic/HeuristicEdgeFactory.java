package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class HeuristicEdgeFactory implements EdgeFactory<HeuristicEdge> {
    private static final HeuristicEdgeFactory INSTANCE = new HeuristicEdgeFactory();

    public static HeuristicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public HeuristicEdge create() {
        return new HeuristicEdge();
    }
}
