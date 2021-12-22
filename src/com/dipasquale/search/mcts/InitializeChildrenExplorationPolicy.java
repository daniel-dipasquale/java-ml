package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InitializeChildrenExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        if (node.getExploredChildren() == null) {
            node.initializeChildren(randomSupport);
        }

        return null;
    }
}
