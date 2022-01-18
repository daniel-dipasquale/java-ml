package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroEdgeFactory implements EdgeFactory<AlphaZeroEdge> {
    private static final AlphaZeroEdgeFactory INSTANCE = new AlphaZeroEdgeFactory();

    public static AlphaZeroEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public AlphaZeroEdge create(final AlphaZeroEdge parent) {
        return new AlphaZeroEdge(parent);
    }
}
