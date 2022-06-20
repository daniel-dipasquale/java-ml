package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import com.dipasquale.synchronization.lock.RcuLock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AbstractRcuLockSearchNodeFactory<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final List<Long> threadIds;
    private final RcuLock edgeLock;
    private final ObjectCloner<TEdge> edgeCloner;

    private AbstractRcuLockSearchNodeFactory(final EdgeFactoryCreator<TEdge> edgeFactoryCreator, final List<Long> threadIds, final RcuLock edgeLock, final ObjectCloner<TEdge> edgeCloner) {
        this(edgeFactoryCreator.create(edgeLock), threadIds, edgeLock, edgeCloner);
    }

    protected AbstractRcuLockSearchNodeFactory(final EdgeFactoryCreator<TEdge> edgeFactoryCreator, final List<Long> threadIds, final ObjectCloner<TEdge> edgeCloner) {
        this(edgeFactoryCreator, threadIds, new RcuLock(threadIds), edgeCloner);
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        return new RcuLockSearchNode<>(result, edgeFactory.create(), threadIds, edgeLock, edgeCloner);
    }

    @FunctionalInterface
    protected interface EdgeFactoryCreator<TEdge extends ConcurrentEdge> {
        EdgeFactory<TEdge> create(RcuLock lock);
    }
}
