package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.AbstractRcuLockSearchNodeFactory;
import com.dipasquale.synchronization.lock.RcuLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Set;

final class RcuLockHeuristicSearchNodeFactory<TAction, TState extends State<TAction, TState>> extends AbstractRcuLockSearchNodeFactory<TAction, ConcurrentHeuristicEdge, TState> {
    private static final EdgeFactoryCreator<ConcurrentHeuristicEdge> EDGE_FACTORY_CREATOR = InternalEdgeFactory::new;
    private static final ObjectCloner<ConcurrentHeuristicEdge> EDGE_CLONER = ConcurrentHeuristicEdge::new;

    RcuLockHeuristicSearchNodeFactory(final Set<Long> threadIds) {
        super(EDGE_FACTORY_CREATOR, threadIds, EDGE_CLONER);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalEdgeFactory implements EdgeFactory<ConcurrentHeuristicEdge> {
        private final RcuLock lock;

        @Override
        public ConcurrentHeuristicEdge create() {
            return new ConcurrentHeuristicEdge(lock);
        }
    }
}
