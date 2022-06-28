package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.common.factory.data.structure.map.concurrent.ConcurrentHashMapFactory;
import com.dipasquale.search.mcts.*;
import com.dipasquale.synchronization.MappedThreadIndex;
import com.dipasquale.synchronization.lock.RcuLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

public abstract class AbstractRcuLockSearchNodeFactory<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final EdgeFactoryCreator<TEdge> edgeFactoryCreator;
    private final Map<Long, EdgeDependency<TEdge>> edgeDependencies;
    private final MappedThreadIndex mappedThreadIndex;
    private final ObjectCloner<TEdge> edgeCloner;

    private AbstractRcuLockSearchNodeFactory(final ConcurrentHashMapFactory mapFactory, final EdgeFactoryCreator<TEdge> edgeFactoryCreator, final List<Long> threadIds, final ObjectCloner<TEdge> edgeCloner) {
        this.edgeFactoryCreator = edgeFactoryCreator;
        this.edgeDependencies = mapFactory.create();
        this.mappedThreadIndex = new MappedThreadIndex(threadIds);
        this.edgeCloner = edgeCloner;
    }

    protected AbstractRcuLockSearchNodeFactory(final EdgeFactoryCreator<TEdge> edgeFactoryCreator, final List<Long> threadIds, final ObjectCloner<TEdge> edgeCloner) {
        this(new ConcurrentHashMapFactory(threadIds.size() + 1), edgeFactoryCreator, threadIds, edgeCloner);
    }

    private EdgeDependency<TEdge> createEdgeDependency(final long threadId) {
        MappedThreadIndex extendedMappedThreadIndex = mappedThreadIndex.extend(List.of(threadId));
        RcuLock edgeLock = new RcuLock(extendedMappedThreadIndex);
        EdgeFactory<TEdge> edgeFactory = edgeFactoryCreator.create(edgeLock);

        return new EdgeDependency<>(edgeLock, edgeFactory);
    }

    @Override
    public EdgeFactory<TEdge> getEdgeFactory() {
        return edgeDependencies.computeIfAbsent(Thread.currentThread().getId(), this::createEdgeDependency).factory;
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> createRoot(final SearchResult<TAction, TState> result) {
        EdgeDependency<TEdge> edgeDependency = edgeDependencies.computeIfAbsent(Thread.currentThread().getId(), this::createEdgeDependency);

        return new RcuLockSearchNode<>(result, edgeDependency.factory.create(), mappedThreadIndex, edgeDependency.lock, edgeCloner);
    }

    @FunctionalInterface
    protected interface EdgeFactoryCreator<TEdge extends ConcurrentEdge> {
        EdgeFactory<TEdge> create(RcuLock lock);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EdgeDependency<T extends ConcurrentEdge> {
        private final RcuLock lock;
        private final EdgeFactory<T> factory;
    }
}
