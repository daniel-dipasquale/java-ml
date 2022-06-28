package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.MappedThreadIndex;

public final class SharedLockSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractConcurrentSearchNode<TAction, TEdge, TState> {
    private final MappedThreadIndex mappedThreadIndex;

    private SharedLockSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final MappedThreadIndex mappedThreadIndex) {
        super(parent, result, edge, mappedThreadIndex);
        this.mappedThreadIndex = mappedThreadIndex;
    }

    SharedLockSearchNode(final SearchResult<TAction, TState> result, final TEdge edge, final MappedThreadIndex mappedThreadIndex) {
        this(null, result, edge, mappedThreadIndex);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new SharedLockSearchNode<>(this, result, edgeFactory.create(), mappedThreadIndex);
    }
}
