package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.EdgeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class SharedLockHeuristicEdgeFactory implements EdgeFactory<ConcurrentHeuristicEdge> {
    private static final boolean FAIR_READ_WRITE_LOCK = false;
    private static final SharedLockHeuristicEdgeFactory INSTANCE = new SharedLockHeuristicEdgeFactory();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(FAIR_READ_WRITE_LOCK);

    public static SharedLockHeuristicEdgeFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ConcurrentHeuristicEdge create() {
        return new ConcurrentHeuristicEdge(lock);
    }
}
