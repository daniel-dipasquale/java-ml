package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InitializeChildrenExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        if (searchNode.getExploredChildren() == null) {
            List<SearchNode<T>> childSearchNodes = searchNode.createAllPossibleChildNodes();

            if (randomSupport != null) {
                randomSupport.shuffle(childSearchNodes);
            }

            searchNode.initializeChildren(childSearchNodes);
        }

        return null;
    }
}
