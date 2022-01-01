package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ClassicDeterministicSelectionPolicy<T extends SearchState> implements SelectionPolicy<T, ClassicSearchEdge> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<T, ClassicSearchEdge> next(final int simulations, final SearchNode<T, ClassicSearchEdge> node) {
        List<SearchNode<T, ClassicSearchEdge>> unexploredChildNodes = node.getUnexploredChildren();
        int unexploredSize = unexploredChildNodes.size();
        List<SearchNode<T, ClassicSearchEdge>> explorableChildNodes = node.getExplorableChildren();
        int explorableSize = explorableChildNodes.size();
        float totalSize = (float) (unexploredSize + explorableSize);

        if (randomSupport.isLessThan((float) unexploredSize / totalSize)) {
            SearchNode<T, ClassicSearchEdge> childNode = unexploredChildNodes.remove(unexploredSize - 1);

            explorableChildNodes.add(childNode);
            childNode.initializeEnvironment();
            node.setChildSelectedIndex(explorableSize);

            return childNode;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<T, ClassicSearchEdge> childNode = explorableChildNodes.get(index);

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        node.setChildSelectedIndex(index);

        return childNode;

    }
}
