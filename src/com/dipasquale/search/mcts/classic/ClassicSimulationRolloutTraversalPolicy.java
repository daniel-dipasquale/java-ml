package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ClassicSimulationRolloutTraversalPolicy<TAction extends Action, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, ClassicEdge, TState> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TAction, ClassicEdge, TState> next(final int simulations, final SearchNode<TAction, ClassicEdge, TState> node) {
        List<SearchNode<TAction, ClassicEdge, TState>> unexploredChildNodes = node.getUnexploredChildren();
        int unexploredSize = unexploredChildNodes.size();
        List<SearchNode<TAction, ClassicEdge, TState>> explorableChildNodes = node.getExplorableChildren();
        int explorableSize = explorableChildNodes.size();
        int totalSize = unexploredSize + explorableSize;

        if (totalSize == 0) {
            return null;
        }

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            SearchNode<TAction, ClassicEdge, TState> childNode = unexploredChildNodes.remove(unexploredSize - 1);

            explorableChildNodes.add(childNode);
            childNode.initializeState();
            node.setSelectedExplorableChildIndex(explorableSize);

            return childNode;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<TAction, ClassicEdge, TState> childNode = explorableChildNodes.get(index);

        node.setSelectedExplorableChildIndex(index);

        return childNode;
    }
}
