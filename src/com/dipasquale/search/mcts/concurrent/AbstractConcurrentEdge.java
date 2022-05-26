package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractConcurrentEdge<T extends Edge> implements ConcurrentEdge {
    private final T edge;
    private final AtomicInteger selectionCount = new AtomicInteger();
    @Getter
    private final ReadWriteLock lock = new ReentrantReadWriteLock(ConcurrentSearchNode.FAIR_READ_WRITE_LOCK);

    @Override
    public int getVisited() {
        return edge.getVisited();
    }

    @Override
    public float getExplorationProbability() {
        return edge.getExplorationProbability();
    }

    @Override
    public void setExplorationProbability(final float value) {
        edge.setExplorationProbability(value);
    }

    @Override
    public void acquireSelection() {
        selectionCount.incrementAndGet();
    }

    @Override
    public int getSelectionCount() {
        return selectionCount.get();
    }

    @Override
    public void releaseSelection() {
        selectionCount.decrementAndGet();
    }
}
