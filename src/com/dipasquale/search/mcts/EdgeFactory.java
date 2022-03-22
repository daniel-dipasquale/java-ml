package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.ObjectFactory;

@FunctionalInterface
public interface EdgeFactory<T extends Edge> extends ObjectFactory<T> {
}
