package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class UnexploredFirstSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> childSearchNodes = searchNode.getUnexploredChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        SearchNode<T> childSearchNode = childSearchNodes.remove(size - 1);

        searchNode.getExplorableChildren().add(childSearchNode);
        childSearchNode.initializeEnvironment();
        searchNode.setChildSelectionIndex(searchNode.getExplorableChildren().size() - 1);

        return childSearchNode;
    }
}
