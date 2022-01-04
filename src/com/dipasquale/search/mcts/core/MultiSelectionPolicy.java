package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiSelectionPolicy<TState extends SearchState, TEdge extends SearchEdge, TEnvironment extends Environment<TState, TEnvironment>> implements SelectionPolicy<TState, TEdge, TEnvironment> {
    private final List<SelectionPolicy<TState, TEdge, TEnvironment>> selectionPolicies;

    @Override
    public SearchNode<TState, TEdge, TEnvironment> next(final int simulations, final SearchNode<TState, TEdge, TEnvironment> node) {
        for (SelectionPolicy<TState, TEdge, TEnvironment> selectionPolicy : selectionPolicies) {
            SearchNode<TState, TEdge, TEnvironment> nextNode = selectionPolicy.next(simulations, node);

            if (nextNode != null) {
                return nextNode;
            }
        }

        return null;
    }
}
