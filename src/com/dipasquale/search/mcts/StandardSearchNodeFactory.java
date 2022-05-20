package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardSearchNodeFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    private static final StandardSearchNodeFactory<?, ?, ?> INSTANCE = new StandardSearchNodeFactory<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> StandardSearchNodeFactory<TAction, TEdge, TState> getInstance() {
        return (StandardSearchNodeFactory<TAction, TEdge, TState>) INSTANCE;
    }

    @Override
    public StandardSearchNode<TAction, TEdge, TState> createRoot(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        return new StandardSearchNode<>(edgeFactory.create(), state);
    }
}
