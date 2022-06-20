package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentHeuristicEdgeFactory implements EdgeFactory<ConcurrentHeuristicEdge> {
    private static final ConcurrentHeuristicEdgeFactory INSTANCE = new ConcurrentHeuristicEdgeFactory();

    public static ConcurrentHeuristicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ConcurrentHeuristicEdge create() {
        return new ConcurrentHeuristicEdge();
    }
}
