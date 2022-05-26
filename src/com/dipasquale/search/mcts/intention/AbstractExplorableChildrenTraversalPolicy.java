package com.dipasquale.search.mcts.intention;

import com.dipasquale.common.Record;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

public abstract class AbstractExplorableChildrenTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    protected abstract int nextIndex(int simulations, TSearchNode parentSearchNode, SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes);

    @Override
    public TSearchNode next(final int simulations, final TSearchNode currentSearchNode) {
        if (!currentSearchNode.isExpanded()) {
            return null;
        }

        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes = currentSearchNode.getExplorableChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = nextIndex(simulations, currentSearchNode, childSearchNodes);
        Record<Integer, TSearchNode> childSearchNodeRecord = childSearchNodes.getRecordByIndex(index);

        currentSearchNode.setSelectedExplorableChildKey(childSearchNodeRecord.getKey());

        return childSearchNodeRecord.getValue();
    }
}
