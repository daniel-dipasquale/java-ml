package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CommonSimulationRolloutTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private final RandomSupport randomSupport;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        List<SearchNode<TAction, TEdge, TState>> unexploredChildren = searchNode.getUnexploredChildren();
        int unexploredSize = unexploredChildren.size();
        List<SearchNode<TAction, TEdge, TState>> explorableChildren = searchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int totalSize = unexploredSize + explorableSize;

        if (totalSize == 0) {
            return null;
        }

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            SearchNode<TAction, TEdge, TState> unexploredChild = unexploredChildren.remove(unexploredSize - 1);

            explorableChildren.add(unexploredChild);
            unexploredChild.initializeState();
            searchNode.setSelectedExplorableChildIndex(explorableSize);

            return unexploredChild;
        }

        int index = randomSupport.next(0, explorableSize);
        SearchNode<TAction, TEdge, TState> explorableChild = explorableChildren.get(index);

        if (explorableChild.getState() == null) {
            explorableChild.initializeState();
        }

        searchNode.setSelectedExplorableChildIndex(index);

        return explorableChild;
    }
}
