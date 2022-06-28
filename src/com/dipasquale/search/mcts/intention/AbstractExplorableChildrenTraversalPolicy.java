package com.dipasquale.search.mcts.intention;

import com.dipasquale.common.Record;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

public abstract class AbstractExplorableChildrenTraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    protected abstract int nextIndex(int simulations, TSearchNode parentSearchNode, SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes);

    protected abstract void expand(final TSearchNode childSearchNode);

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        SearchNodeGroup<TAction, TEdge, TState, TSearchNode> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = nextIndex(simulations, searchNode, childSearchNodes);
        Record<Integer, TSearchNode> childSearchNodeRecord = childSearchNodes.getRecordByIndex(index);
        TSearchNode childSearchNode = childSearchNodeRecord.getValue();

        searchNode.setSelectedExplorableChildKey(childSearchNodeRecord.getKey());
        expand(childSearchNode);

        return childSearchNode;
    }
}
