package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MultiSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private final List<SelectionPolicy<T>> explorationPolicies;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        for (SelectionPolicy<T> selectionPolicy : explorationPolicies) {
            SearchNode<T> nextSearchNode = selectionPolicy.next(simulations, searchNode);

            if (nextSearchNode != null) {
                return nextSearchNode;
            }
        }

        return null;
    }
}
