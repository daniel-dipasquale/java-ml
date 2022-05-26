package com.dipasquale.search.mcts.expansion;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExpansionTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements TraversalPolicy<TAction, TEdge, TState, TSearchNode> {
    private final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy;

    @Override
    public TSearchNode next(final int simulations, final TSearchNode currentSearchNode) {
        if (!currentSearchNode.isExpanded()) {
            expansionPolicy.expand(currentSearchNode);
        }

        return null;
    }
}
