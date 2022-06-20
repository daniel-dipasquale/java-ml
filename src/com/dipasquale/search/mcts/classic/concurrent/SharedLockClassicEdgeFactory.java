package com.dipasquale.search.mcts.classic.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class SharedLockClassicEdgeFactory implements EdgeFactory<ConcurrentClassicEdge> {
    private static final boolean FAIR_READ_WRITE_LOCK = false;
    private static final SharedLockClassicEdgeFactory INSTANCE = new SharedLockClassicEdgeFactory();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(FAIR_READ_WRITE_LOCK);

    public static SharedLockClassicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ConcurrentClassicEdge create() {
        return new ConcurrentClassicEdge(lock);
    }
}
