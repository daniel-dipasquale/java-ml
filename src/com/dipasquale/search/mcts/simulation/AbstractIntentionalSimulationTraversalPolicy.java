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

    protected TSearchNode selectUnexplored(final TSearchNode currentSearchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren = currentSearchNode.getUnexploredChildren();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = currentSearchNode.getExplorableChildren();
        TSearchNode unexploredChild = unexploredChildren.removeByIndex(unexploredChildren.size() - 1);
        int explorableChildKey = explorableChildren.add(unexploredChild);

        currentSearchNode.setSelectedExplorableChildKey(explorableChildKey);

        return unexploredChild;
    }

    protected TSearchNode selectExplorable(final TSearchNode currentSearchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = currentSearchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int index = randomSupport.next(0, explorableSize);
        Record<Integer, TSearchNode> explorableChildRecord = explorableChildren.getRecordByIndex(index);

        currentSearchNode.setSelectedExplorableChildKey(explorableChildRecord.getKey());

        return explorableChildRecord.getValue();
    }

    @Override
    public TSearchNode next(final int simulations, final TSearchNode currentSearchNode) {
        int unexploredSize = currentSearchNode.getUnexploredChildren().size();
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren = currentSearchNode.getExplorableChildren();
        int explorableSize = explorableChildren.size();
        int totalSize = unexploredSize + explorableSize;

        if (randomSupport.isLessThan((float) unexploredSize / (float) totalSize)) {
            return selectUnexplored(currentSearchNode);
        }

        return selectExplorable(currentSearchNode);
    }
}
