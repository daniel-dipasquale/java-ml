package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import com.dipasquale.search.mcts.core.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ClassicSimulationRolloutPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, ClassicEdge, TEnvironment> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TState, ClassicEdge, TEnvironment> next(final int simulations, final SearchNode<TState, ClassicEdge, TEnvironment> node) {
        List<SearchNode<TState, ClassicEdge, TEnvironment>> unexploredChildNodes = node.getUnexploredChildren();
        int unexploredSize = unexploredChildNodes.size();
        List<SearchNode<TState, ClassicEdge, TEnvironment>> explorableChildNodes = node.getExplorableChildren();
        int explorableSize = explorableChildNodes.size();
        int totalSize = unexploredSize + explorableSize;

        if (totalSize == 0) {
            return null;
        }

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            SearchNode<TState, ClassicEdge, TEnvironment> childNode = unexploredChildNodes.remove(unexploredSize - 1);

            explorableChildNodes.add(childNode);
            childNode.initializeEnvironment();
            node.setSelectedExplorableChildIndex(explorableSize);

            return childNode;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<TState, ClassicEdge, TEnvironment> childNode = explorableChildNodes.get(index);

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        node.setSelectedExplorableChildIndex(index);

        return childNode;

    }
}
