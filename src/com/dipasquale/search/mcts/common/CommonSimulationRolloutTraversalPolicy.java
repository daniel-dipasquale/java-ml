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
final class CommonSimulationRolloutTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        List<TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        int unexploredSize = unexploredChildren.size();
        List<TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int totalSize = unexploredSize + explorableSize;

        if (totalSize == 0) {
            return null;
        }

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            TSearchNode unexploredChild = unexploredChildren.remove(unexploredSize - 1);

            explorableChildren.add(unexploredChild);
            unexploredChild.initializeState();
            searchNode.setSelectedExplorableChildIndex(explorableSize);

            return unexploredChild;
        }

        int index = randomSupport.next(0, explorableSize);
        TSearchNode explorableChild = explorableChildren.get(index);

        if (explorableChild.getState() == null) {
            explorableChild.initializeState();
        }

        searchNode.setSelectedExplorableChildIndex(index);

        return explorableChild;
    }
}
