package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.concurrent.AbstractConcurrentEdge;
import com.dipasquale.search.mcts.heuristic.HeuristicEdge;

import java.util.concurrent.locks.ReadWriteLock;

public abstract class AbstractConcurrentHeuristicEdge<T extends HeuristicEdge> extends AbstractConcurrentEdge<T> implements HeuristicEdge {
    private final T edge;

    protected AbstractConcurrentHeuristicEdge(final T edge, final ReadWriteLock lock) {
        super(edge, lock);
        this.edge = edge;
    }

    @Override
    public void increaseVisited() {
        edge.increaseVisited();
    }

    @Override
    public float getExpectedReward() {
        return edge.getExpectedReward();
    }

    @Override
    public void setExpectedReward(final float value) {
        edge.setExpectedReward(value);
    }

    @Override
    public float getProbableReward() {
        return edge.getProbableReward();
    }

    @Override
    public void setProbableReward(final float value) {
        edge.setProbableReward(value);
    }
}
