package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements TraversalPolicy<TAction, TEdge, TState> {
    private final List<TraversalPolicy<TAction, TEdge, TState>> traversalPolicies;

    @Override
    public SearchNode<TAction, TEdge, TState> next(final int simulations, final SearchNode<TAction, TEdge, TState> node) {
        for (TraversalPolicy<TAction, TEdge, TState> traversalPolicy : traversalPolicies) {
            SearchNode<TAction, TEdge, TState> nextNode = traversalPolicy.next(simulations, node);

            if (nextNode != null) {
                return nextNode;
            }
        }

        return null;
    }
}
