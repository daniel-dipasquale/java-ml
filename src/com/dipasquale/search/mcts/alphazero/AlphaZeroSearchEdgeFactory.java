package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroSearchEdgeFactory implements SearchEdgeFactory<AlphaZeroSearchEdge> {
    private static final AlphaZeroSearchEdgeFactory INSTANCE = new AlphaZeroSearchEdgeFactory();

    public static AlphaZeroSearchEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public AlphaZeroSearchEdge create(final AlphaZeroSearchEdge parent) {
        if (parent == null) {
            return new AlphaZeroSearchEdge();
        }

        return new AlphaZeroSearchEdge(parent);
    }
}
