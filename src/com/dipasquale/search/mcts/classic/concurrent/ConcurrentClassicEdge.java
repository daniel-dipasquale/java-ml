package com.dipasquale.search.mcts.classic.concurrent;

import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.StandardClassicEdge;
import com.dipasquale.search.mcts.concurrent.AbstractConcurrentEdge;

import java.util.concurrent.locks.ReadWriteLock;

public final class ConcurrentClassicEdge extends AbstractConcurrentEdge<ClassicEdge> implements ClassicEdge {
    private final StandardClassicEdge edge;

    private ConcurrentClassicEdge(final StandardClassicEdge edge, final ReadWriteLock lock) {
        super(edge, lock);
        this.edge = edge;
    }

    public ConcurrentClassicEdge(final ReadWriteLock lock) {
        this(new StandardClassicEdge(), lock);
    }

    public ConcurrentClassicEdge(final ConcurrentClassicEdge edge) {
        this(new StandardClassicEdge(edge.edge), edge.getLock());
    }

    @Override
    public void increaseVisited() {
        edge.increaseVisited();
    }

    @Override
    public void increaseUnfinished() {
        edge.increaseUnfinished();
    }

    @Override
    public int getWon() {
        return edge.getWon();
    }

    @Override
    public void increaseWon() {
        edge.increaseWon();
    }

    @Override
    public int getDrawn() {
        return edge.getDrawn();
    }

    @Override
    public void increaseDrawn() {
        edge.increaseDrawn();
    }
}
