package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.*;
import com.dipasquale.synchronization.MappedThreadIndex;
import lombok.Getter;

import java.util.List;

public final class SharedLockSearchNodeFactory<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final MappedThreadIndex mappedThreadIndex;

    public SharedLockSearchNodeFactory(final EdgeFactory<TEdge> edgeFactory, final List<Long> threadIds) {
        this.edgeFactory = edgeFactory;
        this.mappedThreadIndex = new MappedThreadIndex(threadIds);
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        return new SharedLockSearchNode<>(result, edgeFactory.create(), mappedThreadIndex);
    }
}
