package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardHeuristicEdgeFactory implements EdgeFactory<StandardHeuristicEdge> {
    private static final StandardHeuristicEdgeFactory INSTANCE = new StandardHeuristicEdgeFactory();

    public static StandardHeuristicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public StandardHeuristicEdge create() {
        return new StandardHeuristicEdge();
    }
}
