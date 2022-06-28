package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.MappedThreadIndex;
import com.dipasquale.synchronization.lock.RcuLock;
import com.dipasquale.synchronization.lock.RcuMonitoredReference;

public final class RcuLockSearchNode<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractConcurrentSearchNode<TAction, TEdge, TState> {
    private final MappedThreadIndex mappedThreadIndex;
    private final RcuLock edgeLock;
    private final ObjectCloner<TEdge> edgeCloner;
    private final RcuMonitoredReference<TEdge> edgeMonitoredReference;

    private RcuLockSearchNode(final ConcurrentSearchNode<TAction, TEdge, TState> parent, final SearchResult<TAction, TState> result, final TEdge edge, final MappedThreadIndex mappedThreadIndex, final RcuLock edgeLock, final ObjectCloner<TEdge> edgeCloner) {
        super(parent, result, null, mappedThreadIndex);
        this.mappedThreadIndex = mappedThreadIndex;
        this.edgeLock = edgeLock;
        this.edgeCloner = edgeCloner;
        this.edgeMonitoredReference = edgeLock.createMonitoredReference(edge, edgeCloner);
    }

    RcuLockSearchNode(final SearchResult<TAction, TState> result, final TEdge edge, final MappedThreadIndex mappedThreadIndex, final RcuLock edgeLock, final ObjectCloner<TEdge> edgeCloner) {
        this(null, result, edge, mappedThreadIndex, edgeLock, edgeCloner);
    }

    @Override
    public TEdge getEdge() {
        return edgeMonitoredReference.get();
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> createChild(final SearchResult<TAction, TState> result, final EdgeFactory<TEdge> edgeFactory) {
        return new RcuLockSearchNode<>(this, result, edgeFactory.create(), mappedThreadIndex, edgeLock, edgeCloner);
    }
}
