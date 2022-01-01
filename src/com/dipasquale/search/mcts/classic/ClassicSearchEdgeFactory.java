package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.SearchEdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicSearchEdgeFactory implements SearchEdgeFactory<ClassicSearchEdge> {
    private static final ClassicSearchEdgeFactory INSTANCE = new ClassicSearchEdgeFactory();

    public static ClassicSearchEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ClassicSearchEdge create(final ClassicSearchEdge parent) {
        if (parent == null) {
            return new ClassicSearchEdge();
        }

        return new ClassicSearchEdge(parent);
    }
}
