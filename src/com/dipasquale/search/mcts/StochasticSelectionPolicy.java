package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StochasticSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> childSearchNodes = searchNode.createAllPossibleChildNodes();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = randomSupport.next(0, size);
        SearchNode<T> childSearchNode = childSearchNodes.get(index);

        childSearchNode.initializeEnvironment();
        searchNode.setChildSelectionIndex(index);

        return childSearchNode;
    }
}
