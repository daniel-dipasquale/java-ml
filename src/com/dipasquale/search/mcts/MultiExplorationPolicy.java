package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MultiExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final List<ExplorationPolicy<T>> explorationPolicies;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        for (ExplorationPolicy<T> explorationPolicy : explorationPolicies) {
            SearchNode<T> nextSearchNode = explorationPolicy.next(simulations, searchNode);

            if (nextSearchNode != null) {
                return nextSearchNode;
            }
        }

        return null;
    }
}
