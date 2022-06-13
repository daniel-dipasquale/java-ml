package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConcurrentSearchNodeFactory<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final int numberOfThreads;

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchNodeResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new ConcurrentSearchNode<>(result, edgeFactory.create(), numberOfThreads);
    }
}
