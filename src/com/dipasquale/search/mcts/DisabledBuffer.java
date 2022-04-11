package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DisabledBuffer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;

    @Override
    public TSearchNode recallOrCreate(final TState state) {
        return searchNodeFactory.createRoot(edgeFactory, state);
    }

    @Override
    public void store(final TSearchNode searchNode) {
    }

    @Override
    public void clear() {
    }
}
