package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiSelectionPolicy<TState extends SearchState, TEdge extends SearchEdge> implements SelectionPolicy<TState, TEdge> {
    private final List<SelectionPolicy<TState, TEdge>> selectionPolicies;

    @Override
    public SearchNode<TState, TEdge> next(final int simulations, final SearchNode<TState, TEdge> node) {
        for (SelectionPolicy<TState, TEdge> selectionPolicy : selectionPolicies) {
            SearchNode<TState, TEdge> nextNode = selectionPolicy.next(simulations, node);

            if (nextNode != null) {
                return nextNode;
            }
        }

        return null;
    }
}
