package com.dipasquale.search.mcts.common;

import com.dipasquale.common.Record;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CommonSimulationRolloutTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) { // TODO: lock here
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        int unexploredSize = unexploredChildren.size();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int totalSize = unexploredSize + explorableSize;

        if (totalSize == 0) {
            return null;
        }

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            TSearchNode unexploredChild = unexploredChildren.removeByIndex(unexploredSize - 1);
            int explorableChildKey = explorableChildren.add(unexploredChild);

            searchNode.setSelectedExplorableChildKey(explorableChildKey);

            return unexploredChild;
        }

        int index = randomSupport.next(0, explorableSize);
        Record<Integer, TSearchNode> explorableChildRecord = explorableChildren.getRecordByIndex(index);

        searchNode.setSelectedExplorableChildKey(explorableChildRecord.getKey());

        return explorableChildRecord.getValue();
    }
}
