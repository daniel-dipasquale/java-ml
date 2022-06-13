package com.dipasquale.search.mcts.simulation;

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

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractIntentionalSimulationTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RandomSupport randomSupport;

    protected TSearchNode selectUnexplored(final TSearchNode searchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = searchNode.getUnexploredChildren();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        TSearchNode unexploredChild = unexploredChildren.removeByIndex(unexploredChildren.size() - 1);
        int explorableChildKey = explorableChildren.add(unexploredChild);

        searchNode.setSelectedExplorableChildKey(explorableChildKey);

        return unexploredChild;
    }

    protected TSearchNode selectExplorable(final TSearchNode searchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int index = randomSupport.next(0, explorableSize);
        Record<Integer, TSearchNode> explorableChildRecord = explorableChildren.getRecordByIndex(index);

        searchNode.setSelectedExplorableChildKey(explorableChildRecord.getKey());

        return explorableChildRecord.getValue();
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        int unexploredSize = searchNode.getUnexploredChildren().size();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = searchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int totalSize = unexploredSize + explorableSize;

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            return selectUnexplored(searchNode);
        }

        return selectExplorable(searchNode);
    }
}
