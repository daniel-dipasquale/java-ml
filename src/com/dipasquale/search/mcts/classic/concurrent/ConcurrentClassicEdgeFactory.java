package com.dipasquale.search.mcts.classic.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentClassicEdgeFactory implements EdgeFactory<ConcurrentClassicEdge> {
    private static final ConcurrentClassicEdgeFactory INSTANCE = new ConcurrentClassicEdgeFactory();

    public static ConcurrentClassicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ConcurrentClassicEdge create() {
        return new ConcurrentClassicEdge();
    }
}
