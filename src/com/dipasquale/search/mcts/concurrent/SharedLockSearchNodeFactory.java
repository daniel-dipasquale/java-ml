package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.IsolatedThreadIndex;
import lombok.Getter;

import java.util.Set;

public final class SharedLockSearchNodeFactory<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final IsolatedThreadIndex isolatedThreadIndex;

    public SharedLockSearchNodeFactory(final EdgeFactory<TEdge> edgeFactory, final Set<Long> threadIds) {
        this.edgeFactory = edgeFactory;
        this.isolatedThreadIndex = new IsolatedThreadIndex(Set.copyOf(threadIds));
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        return new SharedLockSearchNode<>(result, edgeFactory.create(), isolatedThreadIndex);
    }
}
