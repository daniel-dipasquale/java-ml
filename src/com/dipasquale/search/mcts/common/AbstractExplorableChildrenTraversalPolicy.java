package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;

import java.util.List;

public abstract class AbstractExplorableChildrenTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    protected abstract int nextIndex(int simulations, List<SearchNode<TAction, TEdge, TState>> childSearchNodes, TEdge parentEdge);

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        if (!searchNode.isExpanded()) {
            return null;
        }

        List<SearchNode<TAction, TEdge, TState>> childSearchNodes = searchNode.getExplorableChildren();
        int size = childSearchNodes.size();

        if (size == 0) {
            return null;
        }

        int index = nextIndex(simulations, childSearchNodes, searchNode.getEdge());
        SearchNode<TAction, TEdge, TState> childSearchNode = childSearchNodes.get(index);

        if (childSearchNode.getState() == null) {
            childSearchNode.initializeState();
        }

        searchNode.setSelectedExplorableChildIndex(index);

        return childSearchNode;
    }
}
