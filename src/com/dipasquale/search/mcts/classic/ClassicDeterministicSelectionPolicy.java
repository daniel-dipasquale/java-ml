package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import com.dipasquale.search.mcts.core.SelectionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class ClassicDeterministicSelectionPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements SelectionPolicy<TState, ClassicSearchEdge, TEnvironment> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TState, ClassicSearchEdge, TEnvironment> next(final int simulations, final SearchNode<TState, ClassicSearchEdge, TEnvironment> node) {
        List<SearchNode<TState, ClassicSearchEdge, TEnvironment>> unexploredChildNodes = node.getUnexploredChildren();
        int unexploredSize = unexploredChildNodes.size();
        List<SearchNode<TState, ClassicSearchEdge, TEnvironment>> explorableChildNodes = node.getExplorableChildren();
        int explorableSize = explorableChildNodes.size();
        float totalSize = (float) (unexploredSize + explorableSize);

        if (randomSupport.isLessThan((float) unexploredSize / totalSize)) {
            SearchNode<TState, ClassicSearchEdge, TEnvironment> childNode = unexploredChildNodes.remove(unexploredSize - 1);

            explorableChildNodes.add(childNode);
            childNode.initializeEnvironment();
            node.setChildSelectedIndex(explorableSize);

            return childNode;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<TState, ClassicSearchEdge, TEnvironment> childNode = explorableChildNodes.get(index);

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        node.setChildSelectedIndex(index);

        return childNode;

    }
}
