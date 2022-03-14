package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class AlphaZeroEdgeFactory implements EdgeFactory<AlphaZeroEdge> {
    private static final AlphaZeroEdgeFactory INSTANCE = new AlphaZeroEdgeFactory();

    public static AlphaZeroEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public AlphaZeroEdge create(final SearchNode<?, AlphaZeroEdge, ?> node) {
        return new AlphaZeroEdge();
    }
}
