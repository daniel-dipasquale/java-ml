package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;

import java.util.List;

public final class SharedLockSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractConcurrentSearchNode<TAction, TEdge, TState> {
    private final List<Long> threadIds;

    private SharedLockSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final List<Long> threadIds) {
        super(parent, result, edge, threadIds);
        this.threadIds = threadIds;
    }

    SharedLockSearchNode(final SearchResult<TAction, TState> result, final TEdge edge, final List<Long> threadIds) {
        this(null, result, edge, threadIds);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new SharedLockSearchNode<>(this, result, edgeFactory.create(), threadIds);
    }
}
