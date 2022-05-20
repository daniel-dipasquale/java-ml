package com.dipasquale.search.mcts.common;

import com.dipasquale.common.Record;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

public abstract class AbstractExplorableChildrenTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    protected abstract int nextIndex(int simulations, SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes, TSearchNode parentSearchNode);

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        if (!searchNode.isExpanded()) {
            return null;
        }

        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = nextIndex(simulations, childSearchNodes, searchNode);
        Record<Integer, TSearchNode> childSearchNodeRecord = childSearchNodes.getRecordByIndex(index);

        searchNode.setSelectedExplorableChildKey(childSearchNodeRecord.getKey());

        return childSearchNodeRecord.getValue();
    }
}
