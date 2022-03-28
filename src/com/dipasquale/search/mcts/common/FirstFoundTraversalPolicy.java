package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class FirstFoundTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private final List<TraversalPolicy<TAction, TEdge, TState>> traversalPolicies;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> searchNode) {
        for (TraversalPolicy<TAction, TEdge, TState> traversalPolicy : traversalPolicies) {
            SearchNode<TAction, TEdge, TState> nextSearchNode = traversalPolicy.next(simulations, searchNode);

            if (nextSearchNode != null) {
                return nextSearchNode;
            }
        }

        return null;
    }
}
