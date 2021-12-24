package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class UnexploredFirstExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> childSearchNodes = searchNode.getUnexploredChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        SearchNode<T> childSearchNode = childSearchNodes.remove(size - 1);

        searchNode.getExploredChildren().add(childSearchNode);
        childSearchNode.initializeEnvironment();

        return childSearchNode;
    }
}
