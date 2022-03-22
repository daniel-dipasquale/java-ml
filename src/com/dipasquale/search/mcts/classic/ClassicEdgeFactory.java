package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ClassicEdgeFactory implements EdgeFactory<ClassicEdge> {
    private static final ClassicEdgeFactory INSTANCE = new ClassicEdgeFactory();

    public static ClassicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ClassicEdge create() {
        return new ClassicEdge();
    }
}
