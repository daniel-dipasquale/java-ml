package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConcurrentSearchNodeFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final int numberOfThreads;

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final EdgeFactory<TEdge> edgeFactory, final TState state) {
        return new ConcurrentSearchNode<>(edgeFactory.create(), state, numberOfThreads);
    }
}
