package com.dipasquale.search.mcts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StandardSearchNodeFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public StandardSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        return new StandardSearchNode<>(result, edgeFactory.create());
    }
}
