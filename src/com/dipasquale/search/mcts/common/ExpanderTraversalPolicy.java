package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExpanderTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private final Expander<TAction, TEdge, TState> expander;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        if (!searchNode.isExpanded()) {
            expander.expand(searchNode);
        }

        return null;
    }
}
