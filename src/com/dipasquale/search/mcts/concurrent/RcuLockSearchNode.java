package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.lock.RcuLock;
import com.dipasquale.synchronization.lock.RcuMonitoredReference;

import java.util.List;

public final class RcuLockSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractConcurrentSearchNode<TAction, TEdge, TState> {
    private final List<Long> threadIds;
    private final RcuLock edgeLock;
    private final ObjectCloner<TEdge> edgeCloner;
    private final RcuMonitoredReference<TEdge> edgeReference;

    private RcuLockSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final List<Long> threadIds, final RcuLock edgeLock, final ObjectCloner<TEdge> edgeCloner) {
        super(parent, result, null, threadIds);
        this.threadIds = threadIds;
        this.edgeLock = edgeLock;
        this.edgeCloner = edgeCloner;
        this.edgeReference = edgeLock.createMonitoredReference(edge, edgeCloner);
    }

    RcuLockSearchNode(final SearchResult<TAction, TState> result, final TEdge edge, final List<Long> threadIds, final RcuLock edgeLock, final ObjectCloner<TEdge> edgeCloner) {
        this(null, result, edge, threadIds, edgeLock, edgeCloner);
    }

    @Override
    public TEdge getEdge() {
        return edgeReference.get();
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new RcuLockSearchNode<>(this, result, edgeFactory.create(), threadIds, edgeLock, edgeCloner);
    }
}
