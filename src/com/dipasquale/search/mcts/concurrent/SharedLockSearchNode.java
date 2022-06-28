package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.IsolatedThreadIndex;

public final class SharedLockSearchNode<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractConcurrentSearchNode<TAction, TEdge, TState> {
    private final IsolatedThreadIndex isolatedThreadIndex;

    private SharedLockSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final IsolatedThreadIndex isolatedThreadIndex) {
        super(parent, result, edge, isolatedThreadIndex);
        this.isolatedThreadIndex = isolatedThreadIndex;
    }

    SharedLockSearchNode(final SearchResult<TAction, TState> result, final TEdge edge, final IsolatedThreadIndex isolatedThreadIndex) {
        this(null, result, edge, isolatedThreadIndex);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new SharedLockSearchNode<>(this, result, edgeFactory.create(), isolatedThreadIndex);
    }
}
