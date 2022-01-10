package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiTraversalPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> implements TraversalPolicy<TState, TEdge, TEnvironment> {
    private final List<TraversalPolicy<TState, TEdge, TEnvironment>> traversalPolicies;

    @Override
    public SearchNode<TState, TEdge, TEnvironment> next(final int simulations, final SearchNode<TState, TEdge, TEnvironment> node) {
        for (TraversalPolicy<TState, TEdge, TEnvironment> traversalPolicy : traversalPolicies) {
            SearchNode<TState, TEdge, TEnvironment> nextNode = traversalPolicy.next(simulations, node);

            if (nextNode != null) {
                return nextNode;
            }
        }

        return null;
    }
}
