package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

import java.util.List;

public abstract class AbstractExplorableChildrenTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    protected abstract int nextIndex(int simulations, List<TSearchNode> childSearchNodes, TEdge parentEdge);

    @Override
    public TSearchNode next(final int simulations, final TSearchNode searchNode) {
        if (!searchNode.isExpanded()) {
            return null;
        }

        List<TSearchNode> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = nextIndex(simulations, childSearchNodes, searchNode.getEdge());
        TSearchNode childSearchNode = childSearchNodes.get(index);

        if (childSearchNode.getState() == null) {
            childSearchNode.initializeState();
        }

        searchNode.setSelectedExplorableChildIndex(index);

        return childSearchNode;
    }
}
