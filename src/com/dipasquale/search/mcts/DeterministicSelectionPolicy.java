package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DeterministicSelectionPolicy<T extends State> implements SelectionPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> unexploredChildSearchNodes = searchNode.getUnexploredChildren();
        int unexploredSize = unexploredChildSearchNodes.size();
        List<SearchNode<T>> explorableChildSearchNodes = searchNode.getExplorableChildren();
        int explorableSize = explorableChildSearchNodes.size();
        float totalSize = (float) (unexploredSize + explorableSize);

        if (randomSupport.isLessThan((float) unexploredSize / totalSize)) {
            SearchNode<T> childSearchNode = unexploredChildSearchNodes.remove(unexploredSize - 1);

            explorableChildSearchNodes.add(childSearchNode);
            childSearchNode.initializeEnvironment();
            searchNode.setChildSelectionIndex(explorableChildSearchNodes.size() - 1);

            return childSearchNode;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<T> childSearchNode = explorableChildSearchNodes.get(index);

        if (childSearchNode.getEnvironment() == null) {
            childSearchNode.initializeEnvironment();
        }

        searchNode.setChildSelectionIndex(index);

        return childSearchNode;

    }
}
