package com.dipasquale.search.mcts.core;

import com.dipasquale.common.factory.ObjectFactory;

@FunctionalInterface
public interface SearchEdgeFactory<T extends SearchEdge> extends ObjectFactory<T> {
}
