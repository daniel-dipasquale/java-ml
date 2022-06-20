package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractConcurrentEdge<T extends Edge> implements ConcurrentEdge {
    private final T edge;
    @Getter
    private final ReadWriteLock lock;

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
}
