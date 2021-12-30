package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InitializeChildrenSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        if (!searchNode.isExpanded()) {
            List<SearchNode<T>> childSearchNodes = searchNode.createAllPossibleChildNodes();

            if (randomSupport != null) {
                randomSupport.shuffle(childSearchNodes);
            }

            searchNode.setUnexploredChildren(childSearchNodes);
            searchNode.setFullyExploredChildren(new ArrayList<>());
            searchNode.setExplorableChildren(new ArrayList<>());
        }

        return null;
    }
}
