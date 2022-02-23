package com.dipasquale.search.mcts.core;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiSearchNodeProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeProvider<TAction, TEdge, TState> {
    private final List<SearchNodeProvider<TAction, TEdge, TState>> searchNodeProviders;

    @Override
    public SearchNode<TAction, TEdge, TState> provide(final TState state) {
        SearchNode<TAction, TEdge, TState> searchNode = null;

        for (SearchNodeProvider<TAction, TEdge, TState> searchNodeProvider : searchNodeProviders) {
            SearchNode<TAction, TEdge, TState> temporarySearchNode = searchNodeProvider.provide(state);

            if (temporarySearchNode != null) {
                searchNode = temporarySearchNode;
            }
        }

        return searchNode;
    }

    @Override
    public boolean registerIfApplicable(final SearchNode<TAction, TEdge, TState> node) {
        boolean registered = true;

        for (SearchNodeProvider<TAction, TEdge, TState> searchNodeProvider : searchNodeProviders) {
            registered &= searchNodeProvider.registerIfApplicable(node);
        }

        return registered;
    }
}
