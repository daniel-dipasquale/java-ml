package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardClassicEdgeFactory implements EdgeFactory<StandardClassicEdge> {
    private static final StandardClassicEdgeFactory INSTANCE = new StandardClassicEdgeFactory();

    public static StandardClassicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public StandardClassicEdge create() {
        return new StandardClassicEdge();
    }
}
