package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiSearchNodeInitializer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeInitializer<TAction, TEdge, TState> {
    private final List<SearchNodeInitializer<TAction, TEdge, TState>> nodeInitializers;

    @Override
    public boolean apply(final SearchNode<TAction, TEdge, TState> node) {
        boolean applied = true;

        for (SearchNodeInitializer<TAction, TEdge, TState> nodeInitializer : nodeInitializers) {
            applied &= nodeInitializer.apply(node);
        }

        return applied;
    }
}
