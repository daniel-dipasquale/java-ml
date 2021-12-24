package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StatefulRandomExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T> next(final int simulations, final SearchNode<T> searchNode) {
        List<SearchNode<T>> exploredChildSearchNodes = searchNode.getExploredChildren();
        int exploredSize = exploredChildSearchNodes.size();
        List<SearchNode<T>> unexploredChildSearchNodes = searchNode.getUnexploredChildren();
        int unexploredSize = unexploredChildSearchNodes.size();
        float totalSize = (float) (exploredSize + unexploredSize);

        if (randomSupport.isLessThan((float) exploredSize / totalSize)) {
            int index = randomSupport.next(0, exploredSize);

            SearchNode<T> childSearchNode = exploredChildSearchNodes.get(index);

            if (childSearchNode.getEnvironment() == null) {
                childSearchNode.initializeEnvironment();
            }

            return childSearchNode;
        }

        SearchNode<T> childSearchNode = unexploredChildSearchNodes.remove(unexploredSize - 1);

        exploredChildSearchNodes.add(childSearchNode);
        childSearchNode.initializeEnvironment();

        return childSearchNode;
    }
}
