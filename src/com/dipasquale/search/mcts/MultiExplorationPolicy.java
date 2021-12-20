package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class MultiExplorationPolicy<T> implements ExplorationPolicy<T> {
    private final List<ExplorationPolicy<T>> explorationPolicies;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        for (ExplorationPolicy<T> explorationPolicy : explorationPolicies) {
            Node<T> nextNode = explorationPolicy.next(node, simulations);

            if (nextNode != null) {
                return nextNode;
            }
        }

        return null;
    }
}
