package com.dipasquale.search.mcts.classic.concurrent;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.AbstractRcuLockSearchNodeFactory;
import com.dipasquale.synchronization.lock.RcuLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Set;

final class RcuLockClassicSearchNodeFactory<TAction, TState extends State<TAction, TState>> extends AbstractRcuLockSearchNodeFactory<TAction, ConcurrentClassicEdge, TState> {
    private static final EdgeFactoryCreator<ConcurrentClassicEdge> EDGE_FACTORY_CREATOR = InternalEdgeFactory::new;
    private static final ObjectCloner<ConcurrentClassicEdge> EDGE_CLONER = ConcurrentClassicEdge::new;

    RcuLockClassicSearchNodeFactory(final Set<Long> threadIds) {
        super(EDGE_FACTORY_CREATOR, threadIds, EDGE_CLONER);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalEdgeFactory implements EdgeFactory<ConcurrentClassicEdge> {
        private final RcuLock lock;

        @Override
        public ConcurrentClassicEdge create() {
            return new ConcurrentClassicEdge(lock);
        }
    }
}
